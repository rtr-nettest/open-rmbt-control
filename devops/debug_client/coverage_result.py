# Test coverage_result (including a previous request)

import requests
import json
import uuid
import time

def send_coverage_request():
    # Base URL from the API specification
    base_url_v4 = "http://127.0.0.1:8080/RMBTControlServer"
    base_url_v6 = "http://[::1]:8080/RMBTControlServer"
    # use v6
    base_url = base_url_v6

    # Endpoint for coverage registration
    endpoint = "/coverageRequest"
    url = f"{base_url}{endpoint}"

    # Prepare the request body according to the CoverageRegisterRequest schema
    request_body = {
        "client_uuid": "2bda4efd-3007-4c51-a471-cc9db358e7c0",
        "client_language": "en",
        "loop_uuid": "71efe229-b858-4a1d-8f92-47b6698f882e",
        "softwareRevision": "master-632-8bc288a",
        "model": "iPhone17,1",
        "os_version": "18.5",
        "client_name": "RMBT",
        "client_software_version": "4.1",
        "device": "iPhone",
        "platform": "iOS",
        "timezone": "Europe/Prague",
        "time": int(time.time() * 1000),  # Current time in milliseconds
        "measurement_type_flag": "dedicated",
        "signal": False,
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
        print("=" * 60)
        print("STEP 1: Sending Coverage Request")
        print("=" * 60)
        print(f"Sending POST request to: {url}")
        print(f"Request body:\n{json.dumps(request_body, indent=2)}")
        print("-" * 50)

        # Make the POST request
        response = requests.post(url, json=request_body, headers=headers)

        # Print the response
        print(f"Status Code: {response.status_code}")

        if response.status_code == 201:
            print("\n✓ Success! Coverage request created (201 Created)")
            print("Response Body:")
            response_data = response.json()
            print(json.dumps(response_data, indent=2))

            # Extract test_uuid for the second request
            test_uuid = response_data.get("test_uuid")
            if test_uuid:
                print(f"\nExtracted test_uuid: {test_uuid}")
                return test_uuid, base_url
            else:
                print("✗ No test_uuid found in response")
                return None, None

        elif response.status_code == 200:
            print("\n✓ Request successful (200 OK)")
            if response.text:
                print("Response Body:")
                print(json.dumps(response.json(), indent=2))
            else:
                print("Empty response body")
            return None, None
        elif response.status_code == 400:
            print("\n✗ Bad Request (400)")
            if response.text:
                print("Error details:")
                print(json.dumps(response.json(), indent=2))
            return None, None
        else:
            print(f"\n✗ Unexpected status code: {response.status_code}")
            if response.text:
                try:
                    print("Response Body:")
                    print(json.dumps(response.json(), indent=2))
                except:
                    print(f"Response Text: {response.text}")
            return None, None

    except requests.exceptions.ConnectionError:
        print(f"✗ Connection error: Unable to connect to {url}")
        print("Please check if the server is running and accessible.")
        return None, None
    except requests.exceptions.Timeout:
        print("✗ Request timeout: The server took too long to respond.")
        return None, None
    except requests.exceptions.RequestException as e:
        print(f"✗ Request error: {e}")
        return None, None
    except json.JSONDecodeError as e:
        print(f"✗ JSON decode error: {e}")
        if 'response' in locals():
            print(f"Response text: {response.text}")
        return None, None
    except Exception as e:
        print(f"✗ Unexpected error: {type(e).__name__}: {e}")
        return None, None

def send_coverage_result(base_url, test_uuid):
    """Send coverage result after successful coverage request"""
    if not test_uuid or not base_url:
        print("\n✗ Cannot send coverage result: Missing test_uuid or base_url")
        return

    endpoint = "/coverageResult"
    url = f"{base_url}{endpoint}"

    # Prepare the request body according to CoverageResultRequest schema
    current_time_ns = int(time.time() * 1_000_000_000)  # Current time in nanoseconds
    request_body = {
        "test_uuid": test_uuid,
        "sequence_number": 1,
        "time_ns": current_time_ns,
        "client_uuid": "2bda4efd-3007-4c51-a471-cc9db358e7c0",
        "client_version": "4.1",
        "client_language": "en",
        "timezone": "Europe/Prague",
        "platform": "iOS",
        "product": "iPhone17,1",
        "api_level": "18.5",
        "os_version": "18.5",
        "model": "iPhone17,1",
        "device": "iPhone",
        "client_software_version": "4.1",
        "network_type": 99,  # WiFi
        "wifi_supplicant_state": "COMPLETED",
        "wifi_supplicant_state_detail": "OBTAINING_IPADDR",
        "wifi_ssid": "TestWiFi",
        "wifi_network_id": "1",
        "wifi_bssid": "00:11:22:33:44:55",
        "telephony_network_operator": "",
        "telephony_network_is_roaming": False,
        "telephony_network_country": "",
        "telephony_network_operator_name": "",
        "telephony_network_sim_operator_name": "",
        "telephony_network_sim_operator": "",
        "telephony_phone_type": 0,
        "telephony_data_state": 0,
        "telephony_apn": "",
        "telephony_network_sim_country": "",
        "submission_retry_count": 0,
        "test_status": "SUCCESS",
        "test_error_cause": "",
        "capabilitiesRequest": {
            "RMBThttp": True,
            "classification": {
                "count": 5
            },
            "qos": {
                "supports_info": True
            }
        },
        "radioInfo": {
            "cells": [
                {
                    "active": True,
                    "area_code": 12345,
                    "location_id": 67890,
                    "mcc": 310,
                    "mnc": 260,
                    "primary_scrambling_code": 123,
                    "primary_data_subscription": "primary",
                    "registered": True,
                    "technology": "G4",
                    "uuid": str(uuid.uuid4()),
                    "channel_number": 1800,
                    "cell_state": "CONNECTED"
                }
            ],
            "signals": [
                {
                    "bit_error_rate": 0,
                    "cell_uuid": str(uuid.uuid4()),
                    "network_type_id": 99,
                    "signal": -65,
                    "time_ns_last": current_time_ns - 1000000000,
                    "time_ns": current_time_ns,
                    "wifi_link_speed": 65,
                    "lte_rsrp": -85,
                    "lte_rsrq": -10,
                    "lte_rssnr": 15,
                    "nr_ss_rsrp": -75,
                    "nr_ss_rsrq": -8,
                    "nr_ss_sinr": 20,
                    "lte_cqi": 12,
                    "timing_advance": 2
                }
            ]
        },
        "android_permission_status": [],
        "cellLocations": [
            {
                "primary_scrambling_code": 123,
                "time": int(time.time() * 1000),
                "time_ns": current_time_ns,
                "area_code": 12345,
                "location_id": 67890
            }
        ],
        "geoLocations": [
            {
                "accuracy": 10.5,
                "age": 1000,
                "altitude": 150.0,
                "bearing": 90.0,
                "geo_lat": 48.2082,
                "geo_long": 16.3738,
                "mock_location": False,
                "provider": "network",
                "satellites": 8,
                "speed": 0.0,
                "tstamp": int(time.time() * 1000),
                "time_ns": current_time_ns - 5000000000
            }
        ],
        "test_ip_local": "::1",
        "fences": [
            {
                "location": {
                    "latitude": 48.2082,
                    "longitude": 16.3738
                },
                "technology_id": 41,
                "technology": "NR NSA",
                "offset_ms": 13000,
                "duration_ms": 2123,
                "radius": 25.234,
                "avg_ping_ms": 15.4,
                "timestamp_microseconds": int(time.time() * 1_000_000)
            }
        ]
    }

    # Headers for the request
    headers = {
        "Content-Type": "application/json",
        "Accept": "*/*"
    }

    try:
        print("\n" + "=" * 60)
        print("STEP 2: Sending Coverage Result")
        print("=" * 60)
        print(f"Sending POST request to: {url}")
        print(f"Using test_uuid: {test_uuid}")
        print("-" * 50)

        # Make the POST request
        response = requests.post(url, json=request_body, headers=headers)

        # Print the response
        print(f"Status Code: {response.status_code}")
        print(f"Response Headers: {dict(response.headers)}")

        if response.status_code == 200:
            print("\n✓ Success! Coverage result processed (200 OK)")
            if response.text:
                print("Response Body:")
                try:
                    response_data = response.json()
                    print(json.dumps(response_data, indent=2))
                except json.JSONDecodeError:
                    print(f"Response Text: {response.text}")
            else:
                print("Empty response body (expected for successful coverage result)")
        elif response.status_code == 400:
            print("\n✗ Bad Request (400)")
            if response.text:
                print("Error details:")
                try:
                    print(json.dumps(response.json(), indent=2))
                except json.JSONDecodeError:
                    print(f"Response Text: {response.text}")
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
    except requests.exceptions.Timeout:
        print("✗ Request timeout: The server took too long to respond.")
    except requests.exceptions.RequestException as e:
        print(f"✗ Request error: {e}")
    except Exception as e:
        print(f"✗ Unexpected error: {type(e).__name__}: {e}")

if __name__ == "__main__":
    print("=" * 60)
    print("Coverage API Client - Two-Step Process")
    print("=" * 60)

    # Step 1: Send coverage request
    test_uuid, base_url = send_coverage_request()
    # test_uuid = "bd94df94-b629-4af5-bce1-362c64ded9a2"
    # base_url = "http://[::1]:8080/RMBTControlServer"

    # Step 2: Send coverage result (only if first request was successful)
    if test_uuid and base_url:
        # Optional: Add a small delay between requests
        time.sleep(.1)
        send_coverage_result(base_url, test_uuid)
    else:
        print("\n✗ Skipping coverage result: First request was not successful")

    print("\n" + "=" * 60)
    print("Process completed")
    print("=" * 60)
