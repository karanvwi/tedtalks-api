import requests
import time
import sys

BASE_URL = "http://localhost:8080/tedtalks"

def wait_for_server():
    print("Waiting for server...")
    for _ in range(30):
        try:
            requests.get(f"{BASE_URL}/search?q=test")
            print("Server is up!")
            return True
        except:
            time.sleep(1)
    return False

def test_import():
    print("Testing Import...")
    resp = requests.get(f"{BASE_URL}/search?q=climate")
    if resp.status_code == 200 and len(resp.json()) > 0:
        print("Import Verified: Found records.")
        return True
    print("Import Failed:", resp.text)
    return False

def test_influence_year():
    print("Testing Influence per Year...")
    resp = requests.get(f"{BASE_URL}/influence/year")
    if resp.status_code == 200 and len(resp.json()) > 0:
        print("Influence Verified.")
        return True
    print("Influence Failed:", resp.text)
    return False

def test_crud():
    print("Testing CRUD...")
    # Create
    new_talk = {
        "title": "Test CRUD Talk",
        "author": "Test Author",
        "releasedDate": "2023-01-01",
        "viewCount": 1000,
        "likeCount": 100,
        "url": "https://example.com/test"
    }
    resp = requests.post(BASE_URL, json=new_talk)
    if resp.status_code != 200:
        print("Create Failed:", resp.status_code, resp.text)
        return False
    
    # Read to get ID
    resp = requests.get(f"{BASE_URL}/search?q=CRUD")
    data = resp.json()
    if not data:
        print("Read Failed: Not found.")
        return False
    
    record = data[0]
    uuid = record['id']
    print(f"Created Record ID: {uuid}")
    
    # Update
    record['title'] = "Updated Title"
    resp = requests.put(f"{BASE_URL}/{uuid}", json=record)
    if resp.status_code != 200:
        print("Update Failed:", resp.status_code)
        return False
    
    # Verify Update
    resp = requests.get(f"{BASE_URL}/search?q=Updated")
    if not resp.json() or resp.json()[0]['title'] != "Updated Title":
         print("Update Verification Failed")
         return False
         
    # Delete
    resp = requests.delete(f"{BASE_URL}/{uuid}")
    if resp.status_code != 204:
        print("Delete Failed:", resp.status_code)
        return False
        
    # Verify Delete
    resp = requests.get(f"{BASE_URL}/search?q=Updated")
    if resp.json():
        print("Delete Verification Failed: Record still exists")
        return False
        
    print("CRUD Verified.")
    return True

if __name__ == "__main__":
    if not wait_for_server():
        sys.exit(1)
    
    if not test_import(): sys.exit(1)
    if not test_influence_year(): sys.exit(1)
    if not test_crud(): sys.exit(1)
    
    print("ALL TESTS PASSED")
