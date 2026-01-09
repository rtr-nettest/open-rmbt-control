# issue 52, did not return status 200, fixed

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
        "softwareRevision": "master-713-eabcc35",
        "model": "iPhone13,2",
        "os_version": "26.0",
        "client_name": "RMBT",
        "client_software_version": "4.1",
        "device": "iPhone",
        "platform": "iOS",
        "timezone": "Europe/Prague",
        "time": int(time.time() * 1000),
        "measurement_type_flag": "dedicated",
        "signal": False,
        "capabilities": {
            "RMBThttp": True,
            "classification": {
                "count": 4
            },
            "qos": {
                "supports_info": True
            }
        },
        "softwareVersionCode": 17,
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

    # Prepare the request body
    current_time = int(time.time() * 1000000)  # microseconds for timestamp_microseconds

    # Define fences data from your log
    fences = [
        {
            "duration_ms": 71379,
            "technology_id": 13,
            "technology": "4G/LTE",
            "location": {
                "accuracy": 19.00056599040094,
                "altitude": 374.95160966499651,
                "longitude": 13.427369760596321,
                "latitude": 49.701089600906315
            },
            "timestamp_microseconds": 1761573252156650,
            "offset_ms": 21337,
            "radius_m": 20
        },
        {
            "location": {
                "longitude": 13.426996738589198,
                "accuracy": 18.572186700582211,
                "altitude": 370.51160966499651,
                "latitude": 49.701072407821982
            },
            "duration_ms": 70722,
            "radius_m": 20,
            "technology_id": 13,
            "technology": "4G/LTE",
            "timestamp_microseconds": 1761573323536630,
            "offset_ms": 92717
        },
        {
            "duration_ms": 113223,
            "radius_m": 20,
            "offset_ms": 163440,
            "timestamp_microseconds": 1761573394259459,
            "technology_id": 13,
            "technology": "4G/LTE",
            "location": {
                "accuracy": 16.685524364187728,
                "latitude": 49.701318126179778,
                "longitude": 13.426516490682911,
                "altitude": 369.7716096649965
            }
        },
        {
            "duration_ms": 55862,
            "technology": "4G/LTE",
            "offset_ms": 276664,
            "location": {
                "latitude": 49.701203721267383,
                "altitude": 369.7716096649965,
                "longitude": 13.426738126905796,
                "accuracy": 18.36124727426705
            },
            "radius_m": 20,
            "timestamp_microseconds": 1761573507483101,
            "technology_id": 13
        },
        {
            "radius_m": 20,
            "duration_ms": 11002,
            "timestamp_microseconds": 1761573563345276,
            "technology": "4G/LTE",
            "offset_ms": 332526,
            "location": {
                "speed": 1.8600000143051147,
                "latitude": 49.700898947240503,
                "longitude": 13.425798397523748,
                "accuracy": 13.617916752162191,
                "altitude": 365.58163070678711
            },
            "technology_id": 13
        },
        {
            "radius_m": 20,
            "timestamp_microseconds": 1761573574347967,
            "technology": "4G/LTE",
            "location": {
                "accuracy": 17.730604051548156,
                "latitude": 49.700798145055906,
                "heading": 259.80467784530413,
                "longitude": 13.425418695031937,
                "altitude": 365.92464841529727,
                "speed": 0.9300000071525476
            },
            "technology_id": 13,
            "offset_ms": 343529,
            "duration_ms": 14006
        },
        {
            "radius_m": 20,
            "offset_ms": 357535,
            "technology_id": 13,
            "technology": "4G/LTE",
            "location": {
                "longitude": 13.425363757822684,
                "heading": 104.76563296182458,
                "altitude": 365.885952106677,
                "speed": 0.77999997138976462,
                "accuracy": 8.6240552082475652,
                "latitude": 49.700485820702298
            },
            "timestamp_microseconds": 1761573588354099,
            "duration_ms": 20996
        },
        {
            "technology": "4G/LTE",
            "duration_ms": 19049,
            "offset_ms": 378531,
            "radius_m": 20,
            "timestamp_microseconds": 1761573609350402,
            "technology_id": 13,
            "location": {
                "accuracy": 18.004672436612154,
                "heading": 329.41405272144186,
                "speed": 1.820000052452013,
                "latitude": 49.700407462353894,
                "altitude": 365.90578874200583,
                "longitude": 13.42509700274614
            }
        },
        {
            "technology": "4G/LTE",
            "location": {
                "latitude": 49.700223506021253,
                "altitude": 369.7716096649965,
                "accuracy": 12.902450542014002,
                "longitude": 13.424978669774244
            },
            "offset_ms": 397581,
            "technology_id": 13,
            "duration_ms": 477124,
            "timestamp_microseconds": 1761573628400322,
            "radius_m": 20
        },
        {
            "technology_id": 13,
            "timestamp_microseconds": 1761574105525015,
            "location": {
                "altitude": 369.7716096649965,
                "accuracy": 19.596250210639909,
                "longitude": 13.425222616475665,
                "latitude": 49.700319135702308
            },
            "technology": "4G/LTE",
            "offset_ms": 874706,
            "duration_ms": 161701,
            "radius_m": 20
        },
        {
            "technology_id": 41,
            "offset_ms": 1036407,
            "technology": "5G/NRNSA",
            "location": {
                "latitude": 49.700187822627221,
                "longitude": 13.425439804243146,
                "altitude": 369.7716096649965,
                "accuracy": 16.111887522398888
            },
            "duration_ms": 17135,
            "timestamp_microseconds": 1761574267226307,
            "radius_m": 20
        },
        {
            "location": {
                "longitude": 13.42573645525931,
                "altitude": 365.83681869506836,
                "accuracy": 14,
                "latitude": 49.700075550982447,
                "heading": 305.15625,
                "speed": 0.18999999761581421
            },
            "radius_m": 20,
            "timestamp_microseconds": 1761574284361423,
            "offset_ms": 1053542,
            "technology": "5G/NRNSA",
            "technology_id": 41,
            "duration_ms": 20688
        },
        {
            "offset_ms": 1074231,
            "location": {
                "accuracy": 4.7486515301917684,
                "altitude": 369.09914790466428,
                "heading": 86.132820410432601,
                "latitude": 49.700187317414276,
                "longitude": 13.425958889428932,
                "speed": 0.74999999999999489
            },
            "radius_m": 20,
            "technology_id": 41,
            "timestamp_microseconds": 1761574305050408,
            "technology": "5G/NRNSA",
            "duration_ms": 17994
        },
        {
            "duration_ms": 18999,
            "location": {
                "speed": 1.0284587584216778,
                "heading": 62.786276978931227,
                "latitude": 49.700303359182122,
                "longitude": 13.426177712433205,
                "accuracy": 3.9920963090447068,
                "altitude": 367.08428369648755
            },
            "technology": "5G/NRNSA",
            "timestamp_microseconds": 1761574323044855,
            "technology_id": 41,
            "offset_ms": 1092226,
            "radius_m": 20
        },
        {
            "location": {
                "latitude": 49.700373306826329,
                "speed": 1.2311207528253472,
                "altitude": 367.37408334482461,
                "longitude": 13.426450021077967,
                "accuracy": 3.7991306634217996,
                "heading": 52.263789687456516
            },
            "technology": "5G/NRNSA",
            "offset_ms": 1111225,
            "timestamp_microseconds": 1761574342044549,
            "duration_ms": 19009,
            "technology_id": 41,
            "radius_m": 20
        },
        {
            "timestamp_microseconds": 1761574361053946,
            "duration_ms": 14001,
            "location": {
                "altitude": 365.36328129190952,
                "speed": 1.0494451285211439,
                "heading": 50.872425678764081,
                "longitude": 13.426684509414455,
                "latitude": 49.700488203262168,
                "accuracy": 3.593205219643671
            },
            "technology": "5G/NRNSA",
            "offset_ms": 1130235,
            "technology_id": 41,
            "radius_m": 20
        },
        {
            "timestamp_microseconds": 1761574375055521,
            "technology": "5G/NRNSA",
            "duration_ms": 14989,
            "technology_id": 41,
            "location": {
                "accuracy": 3.512844226830838,
                "latitude": 49.70058427433122,
                "longitude": 13.426928144818872,
                "altitude": 365.66862748004496,
                "speed": 1.3436636188873483,
                "heading": 37.980889875112702
            },
            "radius_m": 20,
            "offset_ms": 1144236
        },
        {
            "radius_m": 20,
            "technology_id": 41,
            "offset_ms": 1159226,
            "duration_ms": 16004,
            "technology": "5G/NRNSA",
            "timestamp_microseconds": 1761574390045374,
            "location": {
                "heading": 57.262454849077741,
                "speed": 1.2544592800634766,
                "accuracy": 3.8928464267548382,
                "altitude": 364.56711655203253,
                "longitude": 13.427174630520877,
                "latitude": 49.700682423419067
            }
        },
        {
            "technology_id": 41,
            "timestamp_microseconds": 1761574406049963,
            "location": {
                "speed": 1.4550363274825835,
                "accuracy": 3.9723818236958546,
                "latitude": 49.70078003273624,
                "longitude": 13.427425131372145,
                "altitude": 363.4397393791005,
                "heading": 54.040520608176429
            },
            "radius_m": 20,
            "offset_ms": 1175231,
            "technology": "5G/NRNSA",
            "duration_ms": 15002
        },
        {
            "duration_ms": 35984,
            "offset_ms": 1190233,
            "technology": "5G/NRNSA",
            "radius_m": 20,
            "timestamp_microseconds": 1761574421052531,
            "location": {
                "accuracy": 4.0268825518754738,
                "speed": 1.1607022865520777,
                "longitude": 13.427693726477344,
                "latitude": 49.700851521774013,
                "heading": 72.809294878990372,
                "altitude": 364.07666030339897
            },
            "technology_id": 41
        },
        {
            "offset_ms": 1226218,
            "technology": "5G/NRNSA",
            "technology_id": 41,
            "radius_m": 20,
            "duration_ms": 8138,
            "location": {
                "latitude": 49.70080090438011,
                "longitude": 13.427960474120395,
                "heading": 125.27038962167499,
                "altitude": 365.4376005269587,
                "speed": 0.17162674774168879,
                "accuracy": 4.5194366950783484
            },
            "timestamp_microseconds": 1761574457036857
        }
    ]

    request_body = {
        "client_language": "en",
        "name": "RMBT",
        "plattform": "iOS",
        "platform": "iOS",
        "softwareVersionCode": 17,
        "client": "RMBT",
        "capabilities": {
            "classification": {
                "count": 4
            },
            "RMBThttp": True,
            "qos": {
                "supports_info": True
            }
        },
        "client_name": "RMBT",
        "os_version": "26.0",
        "language": "en",
        "test_uuid": test_uuid,
        "softwareVersion": "4.1",
        "device": "iPhone",
        "softwareRevision": "master-713-eabcc35",
        "client_uuid": "2bda4efd-3007-4c51-a471-cc9db358e7c0",
        "version": "0.3",
        "model": "iPhone13,2",
        "type": "MOBILE",
        "fences": fences,
        "timezone": "Europe/Prague",
        "client_software_version": "4.1",
        # Adding required fields
        "sequence_number": 1,
        "time": int(time.time() * 1000)
    }

    # Headers for the request
    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json, application/*+json"
    }

    try:
        print("\n" + "=" * 60)
        print("STEP 2: Sending Coverage Result")
        print("=" * 60)
        print(f"Sending POST request to: {url}")
        print(f"Using test_uuid: {test_uuid}")
        print(f"Request body size: {len(json.dumps(request_body))} bytes")
        print("-" * 50)

        # Make the POST request
        response = requests.post(url, json=request_body, headers=headers, timeout=60)

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
                print("Empty response body")
        elif response.status_code == 406:
            print("\n✗ Not Acceptable (406)")
            print("The server cannot produce a response matching the list of acceptable values")
            print("defined in the request's proactive content negotiation headers.")
            if response.text:
                print("\nResponse Body:")
                print(response.text)
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
