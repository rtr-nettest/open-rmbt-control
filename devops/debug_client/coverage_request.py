# Client to debug the /coverageRequest endpoint


import requests
import json
import uuid

def send_coverage_request():
    # Base URL from the API specification
    base_url_v4 = "http://127.0.0.1:8080/RMBTControlServer"
    base_url_v6 = "http://[::1]:8080/RMBTControlServer"
    # use v4
    base_url = base_url_v6
    
    # Endpoint for coverage registration
    endpoint = "/coverageRequest"
    url = f"{base_url}{endpoint}"
    
    # Prepare the request body according to the CoverageRegisterRequest schema
    request_body = {
        # "client_uuid": str(uuid.uuid4()),  # Generate a new UUID for this client
        #  "No client found by id 06e13b57-0799-4ec8-b1bb-30e7262c0540."
        # use pre-defined (static) client UUID
        "client_uuid": "2bda4efd-3007-4c51-a471-cc9db358e7c0",
        "client_language": "en",
        "softwareRevision": "master-632-8bc288a",
        "model": "iPhone17,1",
        "os_version": "18.5",
        "client_name": "RMBT",
        "client_software_version": "4.1",
        "device": "iPhone",
        "platform": "iOS",
        "timezone": "Europe/Prague",
        "time": 1571665024591,
        "measurement_type_flag": "dedicated",
        "signal": True,
        "capabilities": {
            "RMBThttp": True,
            "classification": {
                "count": 5
            },
            "qos": {
                "supports_info": True
            }
        },
        "softwareVersionCode": 33201,
        "version": "0.3"
    }
    
    # Headers for the request
    headers = {
        "Content-Type": "application/json",
        "Accept": "*/*"
    }
    
    try:
        print(f"Sending POST request to: {url}")
        print(f"Request body:\n{json.dumps(request_body, indent=2)}")
        print("-" * 50)
        
        # Make the POST request
        response = requests.post(url, json=request_body, headers=headers)
        
        # Print the response
        print(f"Status Code: {response.status_code}")
        print(f"Response Headers: {dict(response.headers)}")
        
        if response.status_code == 201:
            print("\n✓ Success! Coverage request created (201 Created)")
            print("Response Body:")
            print(json.dumps(response.json(), indent=2))
        elif response.status_code == 200:
            print("\n✓ Request successful (200 OK)")
            if response.text:
                print("Response Body:")
                print(json.dumps(response.json(), indent=2))
            else:
                print("Empty response body")
        elif response.status_code == 400:
            print("\n✗ Bad Request (400)")
            if response.text:
                print("Error details:")
                print(json.dumps(response.json(), indent=2))
        else:
            print(f"\n✗ Unexpected status code: {response.status_code}")
            if response.text:
                try:
                    print("Response Body:")
                    print(json.dumps(response.json(), indent=2))
                except:
                    print(f"Response Text: {response.text}")
    
    except requests.exceptions.ConnectionError:
        print(f"✗ Connection error: Unable to connect to {url}")
        print("Please check if the server is running and accessible.")
    except requests.exceptions.Timeout:
        print("✗ Request timeout: The server took too long to respond.")
    except requests.exceptions.RequestException as e:
        print(f"✗ Request error: {e}")
    except json.JSONDecodeError as e:
        print(f"✗ JSON decode error: {e}")
        print(f"Response text: {response.text}")
    except Exception as e:
        print(f"✗ Unexpected error: {type(e).__name__}: {e}")

if __name__ == "__main__":
    print("=" * 60)
    print("Coverage Request API Client")
    print("=" * 60)
    send_coverage_request()

# Sample response

#     Response Body:
#     {
#       "client_remote_ip": "0:0:0:0:0:0:0:1",
#       "provider": null,
#       "test_uuid": "ddeaf12e-6dc6-4da9-85a5-6641147228c9",
#       "ping_token": "aWDugZx87sZN5ZpmMxV+WQ==",
#       "ping_host": "udpv6.netztest.at",
#       "ping_port": "444",
#       "ip_version": 6,
#       "max_coverage_session_seconds": 14400,
#       "max_coverage_measurement_seconds": 3600
#     }

#
