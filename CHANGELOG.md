# ExploreBangladesh - Recent Updates

## Version 1.2.0 - January 27, 2026

### ✈️ New Feature: Flight Search (Amadeus API)

#### Flight Search Page (`flights.html`)
- **Real flight data** from Amadeus Flight Offers Search API
- Search flights between 8 Bangladesh airports + 10 international destinations
- **One-way and Round-trip** options with toggle buttons
- Swap origin/destination with single click
- Flight cards showing:
  - Airline name & logo
  - Departure/arrival times
  - Flight duration & stops
  - Price in BDT (৳)
  - "Select" button (opens Google booking search)

#### Supported Airports
| Bangladesh (8) | International (10) |
|----------------|-------------------|
| Dhaka (DAC) | Dubai (DXB) |
| Chittagong (CGP) | Singapore (SIN) |
| Cox's Bazar (CXB) | Bangkok (BKK) |
| Sylhet (ZYL) | Kuala Lumpur (KUL) |
| Rajshahi (RJH) | Delhi (DEL) |
| Jessore (JSR) | Kolkata (CCU) |
| Saidpur (SPD) | Doha (DOH) |
| Barishal (BZL) | Jeddah (JED) |
| | London (LHR) |
| | New York (JFK) |

#### Homepage Integration
- Flights tab now has **proper airport dropdowns**
- Swap button, travelers select, departure date
- Search redirects to flights.html with auto-search

---

### 🔧 Technical Changes

#### New Backend Files
- `FlightsApiService.java` - Amadeus OAuth + flight search
- `FlightsController.java` - REST endpoint `/api/flights/search`
- `FlightSearchRequest.java` - Request DTO
- `FlightSearchResponse.java` - Response DTO with nested classes
- `CorsConfig.java` - CORS support for Live Server

#### New Frontend Files
- `flights.html` - Flight search page
- `flights.js` - Search logic, URL param handling, card rendering

#### Updated Files
- `index.html` - Flights tab with dropdowns, redirect logic
- `SecurityConfig.java` - Added flights.html to permitted resources
- `application.properties.template` - Added Amadeus API placeholders

---

### 🔐 Security Notes

> **IMPORTANT**: `application.properties` is in `.gitignore` and will NOT be pushed.

Your teammates need to:
1. Copy `application.properties.template` to `application.properties`
2. Replace `YOUR_API_KEY_HERE` with actual API keys

API Keys needed:
- **Geoapify** (Hotels): https://www.geoapify.com/
- **Amadeus** (Flights): https://developers.amadeus.com/

---

## Version 1.1.0 - January 27, 2026

### 🏨 Hotel Search (Geoapify API)

#### Hotel Search Page (`hotels.html`)
- **Real hotel data** from Geoapify Places API
- Search hotels across 12 Bangladesh districts
- Hotel cards with curated images, names, and addresses
- "View Details" opens Google search for booking

#### Interactive Map View (Leaflet.js)
- **"View in a map"** feature like Hotels.com
- Static map preview in sidebar (Geoapify Static Maps)
- Full interactive map modal with hotel markers
- Click markers for hotel info popups
- Auto-zoom to fit all hotel locations

#### Supported Districts
| District | District | District |
|----------|----------|----------|
| Dhaka | Cox's Bazar | Chittagong |
| Sylhet | Khulna | Rajshahi |
| Rangpur | Barishal | Mymensingh |
| Rangamati | Bandarban | Tangail |

#### Homepage Integration
- Hotels tab with destination autocomplete
- Check-in/Check-out date pickers (auto-filled)
- Guest count selector
- Search redirects to hotels.html with auto-search

---

### 🔧 Technical Changes (v1.1.0)

#### Backend Files
- `HotelsApiService.java` - Geoapify API integration
- `HotelsController.java` - REST endpoint `/api/hotels/search`
- `HotelSearchRequest.java` / `HotelSearchResponse.java` - DTOs
- `SecurityConfig.java` - Spring Security configuration

#### Frontend Files
- `hotels.html` - Hotel search page with map modal
- `hotels.js` - Search logic, map integration, card rendering

#### Dependencies
- **Leaflet.js 1.9.4** - Interactive maps (CDN, no API key)
- **OpenStreetMap** tiles for map display

---

## How to Run

```bash
# 1. Copy template and add your API keys
copy application.properties.template src\main\resources\application.properties

# 2. Start the application
.\mvnw.cmd spring-boot:run

# 3. Access at
http://localhost:8080
```

---

*Updated by: Team Deadlock*
