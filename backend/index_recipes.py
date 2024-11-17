import json
from opensearchpy import helpers, OpenSearch

# Replace with your OpenSearch service URI
SERVICE_URI = 'http://localhost:9200'
INDEX_NAME = 'recipes'

# Initialize the OpenSearch client
os_client = OpenSearch(
    hosts=[SERVICE_URI],
    http_auth=('admin', 'Iphos_Task_123!'),  # Replace with your actual credentials
    use_ssl=True,
    verify_certs=False,  # Set to False if you are using self-signed certificates
    ssl_show_warn=False
)

# Define the index mappings
index_mappings = {
    "mappings": {
        "properties": {
            "title": { "type": "text" },
            "desc": { "type": "text" },
            "date": { "type": "date" },
            "categories": { "type": "keyword" },
            "ingredients": { "type": "keyword" },
            "directions": { "type": "keyword" },
            "calories": { "type": "float" },
            "fat": { "type": "float" },
            "protein": { "type": "float" },
            "rating": { "type": "float" },
            "sodium": { "type": "float" }
        }
    }
}

# Create the index with the specified mappings
os_client.indices.create(index=INDEX_NAME, body=index_mappings)

def load_data():
    with open('full_format_recipes.json', 'r') as f:
        data = json.load(f)
        for recipe in data:
            yield {'_index': INDEX_NAME, '_source': recipe}

# Use the bulk helper to index the data
helpers.bulk(os_client, load_data())