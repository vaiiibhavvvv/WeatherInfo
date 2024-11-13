# Weather Information API

A Spring Boot application that provides a REST API to retrieve weather information for a specific pincode and date. The application integrates with the OpenWeather API to get weather data and uses OpenWeather's Geocoding API to convert pincode to latitude and longitude. It also stores the data in a MySQL database for optimized future API calls.

## API Endpoint

### `GET /weather`

Fetches weather information based on the provided pincode and date. If the weather information for the pincode and date is already stored in the database, it returns the cached data. If not, it makes a call to the OpenWeather API, stores the data in the database, and returns the result.

#### Request

##### Example API Request
GET http://localhost:8080/weather?pincode={pincode}&for_date={date}

##### Example API Response
json
Copy code
{
  "pincode": 110095,
  "place": "Jhilmil",
  "date": "2024-11-13",
  "temperature": 86.0,
  "humidity": 58,
  "pressure": 29.88,
  "windSpeed": 2.0,
  "description": "Sunny"
}
