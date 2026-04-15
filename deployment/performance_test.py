# Performance Testing Script for TDSE Twitter Application

import requests
import time
import threading
import statistics
from concurrent.futures import ThreadPoolExecutor, as_completed

class PerformanceTester:
    def __init__(self, base_url="http://localhost:8080/api"):
        self.base_url = base_url
        self.results = []
        
    def test_endpoint(self, endpoint, method="GET", data=None):
        """Test a single endpoint and return response time"""
        url = f"{self.base_url}{endpoint}"
        start_time = time.time()
        
        try:
            if method == "GET":
                response = requests.get(url, timeout=10)
            elif method == "POST":
                response = requests.post(url, json=data, timeout=10)
            else:
                return None
                
            end_time = time.time()
            response_time = (end_time - start_time) * 1000  # Convert to milliseconds
            
            return {
                "endpoint": endpoint,
                "method": method,
                "status_code": response.status_code,
                "response_time": response_time,
                "success": response.status_code == 200
            }
        except Exception as e:
            end_time = time.time()
            return {
                "endpoint": endpoint,
                "method": method,
                "status_code": 0,
                "response_time": (end_time - start_time) * 1000,
                "success": False,
                "error": str(e)
            }
    
    def load_test_endpoint(self, endpoint, method="GET", data=None, concurrent_users=10, requests_per_user=5):
        """Perform load testing on an endpoint"""
        print(f"Load testing {method} {endpoint} with {concurrent_users} concurrent users, {requests_per_user} requests each...")
        
        all_results = []
        
        def user_requests():
            user_results = []
            for _ in range(requests_per_user):
                result = self.test_endpoint(endpoint, method, data)
                if result:
                    user_results.append(result)
                time.sleep(0.1)  # Small delay between requests
            return user_results
        
        with ThreadPoolExecutor(max_workers=concurrent_users) as executor:
            futures = [executor.submit(user_requests) for _ in range(concurrent_users)]
            
            for future in as_completed(futures):
                try:
                    user_results = future.result()
                    all_results.extend(user_results)
                except Exception as e:
                    print(f"User request failed: {e}")
        
        return all_results
    
    def analyze_results(self, results):
        """Analyze test results and return statistics"""
        if not results:
            return {}
        
        successful_results = [r for r in results if r["success"]]
        failed_results = [r for r in results if not r["success"]]
        
        response_times = [r["response_time"] for r in successful_results]
        
        if response_times:
            return {
                "total_requests": len(results),
                "successful_requests": len(successful_results),
                "failed_requests": len(failed_results),
                "success_rate": (len(successful_results) / len(results)) * 100,
                "avg_response_time": statistics.mean(response_times),
                "min_response_time": min(response_times),
                "max_response_time": max(response_times),
                "median_response_time": statistics.median(response_times),
                "p95_response_time": response_times[int(len(response_times) * 0.95)],
                "p99_response_time": response_times[int(len(response_times) * 0.99)]
            }
        else:
            return {
                "total_requests": len(results),
                "successful_requests": 0,
                "failed_requests": len(failed_results),
                "success_rate": 0
            }
    
    def run_performance_tests(self):
        """Run comprehensive performance tests"""
        print("Starting Performance Tests for TDSE Twitter Application")
        print("=" * 60)
        
        test_scenarios = [
            {"endpoint": "/health", "method": "GET", "users": 20, "requests": 10},
            {"endpoint": "/posts", "method": "GET", "users": 15, "requests": 8},
            {"endpoint": "/users", "method": "GET", "users": 10, "requests": 5},
            {"endpoint": "/stream", "method": "GET", "users": 25, "requests": 12},
        ]
        
        all_test_results = {}
        
        for scenario in test_scenarios:
            print(f"\nTesting: {scenario['method']} {scenario['endpoint']}")
            print("-" * 40)
            
            results = self.load_test_endpoint(
                scenario["endpoint"],
                scenario["method"],
                concurrent_users=scenario["users"],
                requests_per_user=scenario["requests"]
            )
            
            stats = self.analyze_results(results)
            all_test_results[f"{scenario['method']} {scenario['endpoint']}"] = stats
            
            # Print results
            print(f"Total Requests: {stats.get('total_requests', 0)}")
            print(f"Success Rate: {stats.get('success_rate', 0):.2f}%")
            if stats.get('avg_response_time'):
                print(f"Average Response Time: {stats['avg_response_time']:.2f}ms")
                print(f"95th Percentile: {stats['p95_response_time']:.2f}ms")
                print(f"99th Percentile: {stats['p99_response_time']:.2f}ms")
        
        # Summary
        print("\n" + "=" * 60)
        print("PERFORMANCE TEST SUMMARY")
        print("=" * 60)
        
        for test_name, stats in all_test_results.items():
            print(f"\n{test_name}:")
            print(f"  Success Rate: {stats.get('success_rate', 0):.2f}%")
            if stats.get('avg_response_time'):
                print(f"  Avg Response Time: {stats['avg_response_time']:.2f}ms")
                print(f"  95th Percentile: {stats['p95_response_time']:.2f}ms")
        
        return all_test_results

def main():
    """Main function to run performance tests"""
    tester = PerformanceTester()
    
    try:
        # First check if the application is running
        print("Checking if application is running...")
        health_check = tester.test_endpoint("/health")
        
        if not health_check or not health_check["success"]:
            print("Application is not running or not healthy!")
            print("Please start the application before running performance tests.")
            return
        
        print("Application is healthy. Starting performance tests...")
        
        # Run performance tests
        results = tester.run_performance_tests()
        
        print("\nPerformance tests completed!")
        
    except KeyboardInterrupt:
        print("\nPerformance tests interrupted by user.")
    except Exception as e:
        print(f"Error during performance testing: {e}")

if __name__ == "__main__":
    main()
