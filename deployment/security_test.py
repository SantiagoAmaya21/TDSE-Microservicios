# Security Testing Script for TDSE Twitter Application

import requests
import json
import time

class SecurityTester:
    def __init__(self, base_url="http://localhost:8080/api"):
        self.base_url = base_url
        self.session = requests.Session()
        
    def test_sql_injection(self):
        """Test for SQL injection vulnerabilities"""
        print("Testing SQL Injection vulnerabilities...")
        
        sql_payloads = [
            "' OR '1'='1",
            "' OR '1'='1' --",
            "' OR '1'='1' /*",
            "admin'--",
            "admin'/*",
            "' OR 1=1--",
            "' OR 1=1#",
            "' OR 1=1/*",
            "') OR '1'='1--",
            "') OR ('1'='1--"
        ]
        
        vulnerable_endpoints = [
            "/users",
            "/posts"
        ]
        
        vulnerabilities = []
        
        for endpoint in vulnerable_endpoints:
            for payload in sql_payloads:
                try:
                    # Test with parameter
                    test_url = f"{self.base_url}{endpoint}/{payload}"
                    response = self.session.get(test_url, timeout=5)
                    
                    # Check for SQL error messages
                    sql_errors = [
                        "SQL syntax",
                        "mysql_fetch",
                        "ora-",
                        "Microsoft OLE DB",
                        "ODBC Microsoft Access",
                        "ODBC SQL Server Driver",
                        "SQLServer JDBC Driver",
                        "PostgreSQL query failed"
                    ]
                    
                    response_text = response.text.lower()
                    if any(error.lower() in response_text for error in sql_errors):
                        vulnerabilities.append({
                            "type": "SQL Injection",
                            "endpoint": endpoint,
                            "payload": payload,
                            "response_code": response.status_code,
                            "evidence": "SQL error message in response"
                        })
                        
                except Exception as e:
                    continue
        
        return vulnerabilities
    
    def test_xss(self):
        """Test for XSS vulnerabilities"""
        print("Testing XSS vulnerabilities...")
        
        xss_payloads = [
            "<script>alert('XSS')</script>",
            "javascript:alert('XSS')",
            "<img src=x onerror=alert('XSS')>",
            "';alert('XSS');//",
            "<svg onload=alert('XSS')>",
            "';alert(String.fromCharCode(88,83,83))//"
        ]
        
        vulnerable_endpoints = [
            "/posts"  # POST endpoint that might accept user input
        ]
        
        vulnerabilities = []
        
        for endpoint in vulnerable_endpoints:
            for payload in xss_payloads:
                try:
                    # Test POST request with XSS payload
                    test_data = {"content": payload}
                    response = self.session.post(
                        f"{self.base_url}{endpoint}",
                        json=test_data,
                        timeout=5
                    )
                    
                    # Check if XSS payload is reflected in response
                    if payload in response.text:
                        vulnerabilities.append({
                            "type": "XSS",
                            "endpoint": endpoint,
                            "payload": payload,
                            "response_code": response.status_code,
                            "evidence": "XSS payload reflected in response"
                        })
                        
                except Exception as e:
                    continue
        
        return vulnerabilities
    
    def test_authentication_bypass(self):
        """Test for authentication bypass vulnerabilities"""
        print("Testing authentication bypass vulnerabilities...")
        
        protected_endpoints = [
            "/api/users/me",
            "/api/posts"  # POST method
        ]
        
        vulnerabilities = []
        
        # Test access without authentication
        for endpoint in protected_endpoints:
            try:
                response = self.session.get(f"{self.base_url}{endpoint}", timeout=5)
                
                # Should return 401/403 for protected endpoints
                if response.status_code not in [401, 403]:
                    vulnerabilities.append({
                        "type": "Authentication Bypass",
                        "endpoint": endpoint,
                        "method": "GET",
                        "response_code": response.status_code,
                        "evidence": f"Expected 401/403, got {response.status_code}"
                    })
                    
            except Exception as e:
                continue
        
        # Test with fake JWT token
        fake_token = "fake.jwt.token"
        headers = {"Authorization": f"Bearer {fake_token}"}
        
        for endpoint in protected_endpoints:
            try:
                response = self.session.get(f"{self.base_url}{endpoint}", headers=headers, timeout=5)
                
                # Should return 401 for invalid token
                if response.status_code != 401:
                    vulnerabilities.append({
                        "type": "Authentication Bypass",
                        "endpoint": endpoint,
                        "method": "GET",
                        "response_code": response.status_code,
                        "evidence": "Fake JWT token accepted"
                    })
                    
            except Exception as e:
                continue
        
        return vulnerabilities
    
    def test_cors_configuration(self):
        """Test CORS configuration"""
        print("Testing CORS configuration...")
        
        try:
            # Test OPTIONS request
            response = self.session.options(f"{self.base_url}/posts", timeout=5)
            
            cors_headers = [
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Methods",
                "Access-Control-Allow-Headers"
            ]
            
            missing_headers = []
            for header in cors_headers:
                if header not in response.headers:
                    missing_headers.append(header)
            
            if missing_headers:
                return [{
                    "type": "CORS Misconfiguration",
                    "endpoint": "/posts",
                    "evidence": f"Missing CORS headers: {', '.join(missing_headers)}"
                }]
            
            # Check if wildcard is used (potential security issue)
            allow_origin = response.headers.get("Access-Control-Allow-Origin", "")
            if allow_origin == "*":
                return [{
                    "type": "CORS Misconfiguration",
                    "endpoint": "/posts",
                    "evidence": "Wildcard CORS policy allows any origin"
                }]
                
        except Exception as e:
            return [{
                "type": "CORS Misconfiguration",
                "endpoint": "/posts",
                "evidence": f"CORS test failed: {str(e)}"
            }]
        
        return []
    
    def test_rate_limiting(self):
        """Test for rate limiting"""
        print("Testing rate limiting...")
        
        endpoint = "/posts"
        request_count = 100
        
        success_count = 0
        rate_limited = False
        
        for i in range(request_count):
            try:
                response = self.session.get(f"{self.base_url}{endpoint}", timeout=2)
                
                if response.status_code == 429:
                    rate_limited = True
                    break
                elif response.status_code == 200:
                    success_count += 1
                    
                # Small delay to avoid overwhelming the server
                time.sleep(0.01)
                
            except Exception:
                continue
        
        if not rate_limited and success_count > 50:
            return [{
                "type": "Missing Rate Limiting",
                "endpoint": endpoint,
                "evidence": f"Made {success_count} successful requests without rate limiting"
            }]
        
        return []
    
    def test_information_disclosure(self):
        """Test for information disclosure"""
        print("Testing for information disclosure...")
        
        sensitive_endpoints = [
            "/actuator",
            "/actuator/health",
            "/actuator/info",
            "/h2-console",
            "/.env",
            "/web.xml",
            "/application.properties"
        ]
        
        vulnerabilities = []
        
        for endpoint in sensitive_endpoints:
            try:
                response = self.session.get(f"{self.base_url.replace('/api', '')}{endpoint}", timeout=5)
                
                if response.status_code == 200:
                    # Check if response contains sensitive information
                    sensitive_patterns = [
                        "password",
                        "secret",
                        "key",
                        "token",
                        "database",
                        "connection"
                    ]
                    
                    response_text = response.text.lower()
                    if any(pattern in response_text for pattern in sensitive_patterns):
                        vulnerabilities.append({
                            "type": "Information Disclosure",
                            "endpoint": endpoint,
                            "response_code": response.status_code,
                            "evidence": "Sensitive information exposed"
                        })
                        
            except Exception:
                continue
        
        return vulnerabilities
    
    def run_security_tests(self):
        """Run all security tests"""
        print("Starting Security Tests for TDSE Twitter Application")
        print("=" * 60)
        
        all_vulnerabilities = {}
        
        test_methods = [
            ("SQL Injection", self.test_sql_injection),
            ("XSS", self.test_xss),
            ("Authentication Bypass", self.test_authentication_bypass),
            ("CORS Configuration", self.test_cors_configuration),
            ("Rate Limiting", self.test_rate_limiting),
            ("Information Disclosure", self.test_information_disclosure)
        ]
        
        for test_name, test_method in test_methods:
            print(f"\n{'='*20} {test_name} {'='*20}")
            vulnerabilities = test_method()
            all_vulnerabilities[test_name] = vulnerabilities
            
            if vulnerabilities:
                print(f"Found {len(vulnerabilities)} vulnerabilities:")
                for vuln in vulnerabilities:
                    print(f"  - {vuln['type']} in {vuln['endpoint']}: {vuln['evidence']}")
            else:
                print("No vulnerabilities found")
        
        # Summary
        print("\n" + "=" * 60)
        print("SECURITY TEST SUMMARY")
        print("=" * 60)
        
        total_vulnerabilities = sum(len(vulns) for vulns in all_vulnerabilities.values())
        
        if total_vulnerabilities == 0:
            print("No security vulnerabilities found! \ud83c\udf89")
        else:
            print(f"Found {total_vulnerabilities} potential security issues:")
            
            for test_name, vulnerabilities in all_vulnerabilities.items():
                if vulnerabilities:
                    print(f"\n{test_name}: {len(vulnerabilities)} issues")
                    for vuln in vulnerabilities:
                        print(f"  - {vuln['endpoint']}: {vuln['evidence']}")
        
        return all_vulnerabilities

def main():
    """Main function to run security tests"""
    tester = SecurityTester()
    
    try:
        # Check if application is running
        print("Checking if application is running...")
        health_check = tester.session.get(f"{tester.base_url}/health", timeout=5)
        
        if health_check.status_code != 200:
            print("Application is not running or not healthy!")
            print("Please start the application before running security tests.")
            return
        
        print("Application is healthy. Starting security tests...")
        
        # Run security tests
        results = tester.run_security_tests()
        
        print("\nSecurity tests completed!")
        
    except KeyboardInterrupt:
        print("\nSecurity tests interrupted by user.")
    except Exception as e:
        print(f"Error during security testing: {e}")

if __name__ == "__main__":
    main()
