## Web-Crawl
To Crawl the Web urls and store in Apace Solr and retrieve from Apache Solr.

## Supported Services

1) ### Store Crawl Data
Crawl the listed web urls data from world wide web and store the links, metadata and title from the web page.

PUT {{crawlUrl}}/webcrawl <br>
Host: localhost:7547 <br>
Content-Type: application/json <br>

##### Payload:
{
"webUrls": [
"https://www.javainuse.com/",
"https://www.dbs.com"
]
}

##### Success Response:
{
"success": true,
"message": "Web crawl done successfully",
"data": [
"https://www.javainuse.com/",
"https://www.dbs.com"
]
}

##### Error Response:

{
"messages": [
"webUrls : URL must not be empty"
],
"status": "400 BAD_REQUEST"
}

2) ### Fetch Crawl Data
Fetch crawl data to get list of crawl information stored using full text search.

GET /webcrawl?anySearchText=java
Host: localhost:7547

##### Success Response:
{
"success": true,
"message": "Web crawl fetched successfully",
"data": [
{
"id": "e4f3d19b-e1a4-4887-a1f8-07023d539d67",
"url": "/java",
"title": "Home | JavaInUse",
"keyword": [
"Java"
],
"description": "Java",
"linkedUrls": null,
"parentId": "2cbf899b-cf9b-4372-91b8-6f24b045cc50"
},
{
"id": "54940152-e396-4deb-83c5-00f8d670eac6",
"url": "/java/java8_intvw",
"title": "Home | JavaInUse",
"keyword": [
"Java 8 Interview Questions"
],
"description": "Java 8 Interview Questions",
"linkedUrls": null,
"parentId": "2cbf899b-cf9b-4372-91b8-6f24b045cc50"
}]}

##### No Records Response:
{
"messages": [
"No records found with match data e3tr"
],
"status": "404 NOT_FOUND"
}

