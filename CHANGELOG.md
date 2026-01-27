# ExploreBangladesh - Recent Updates

## Version 1.1.0 - January 27, 2026

### 🗺️ New Features

#### Interactive Map View (Leaflet.js)
- Added **"View in a map"** feature like Hotels.com
- Static map preview in sidebar (powered by Geoapify)
- Click to open full interactive map modal
- Hotel markers with popups showing name, address, and "View Details" button
- Map auto-zooms to fit all hotel locations

#### Enhanced Hotel Details
- "View Details" now performs a **Google search** for the hotel
- Search includes destination for better accuracy (e.g., "Hotel Name hotel Dhaka Bangladesh")

#### Expanded District Support
Added support for the following districts:
| District | Coordinates |
|----------|-------------|
| Dhaka | 90.4125, 23.8103 |
| Cox's Bazar | 91.9670, 21.4272 |
| Chittagong | 91.7832, 22.3569 |
| Sylhet | 91.8687, 24.8949 |
| Khulna | 89.5690, 22.8456 |
| Rajshahi | 88.6042, 24.3745 |
| Rangpur | 89.2517, 25.7439 |
| Barishal | 90.3667, 22.7010 |
| Mymensingh | 90.4066, 24.7471 |
| Rangamati | 92.1821, 22.6372 |
| Bandarban | 92.2184, 22.1953 |
| **Tangail** *(new)* | 89.9168, 24.2513 |

---

### 🎨 UI/UX Improvements

#### Homepage
- Date fields auto-fill with tomorrow (check-in) and day after (check-out)
- Search form redirects to hotels.html with URL parameters
- Destination datalist updated with all supported districts

#### Hotels Page
- **Removed filter sidebar** (Price Range, Star Rating, Sort By)
- Added **Hotels.com style map preview** card in sidebar
- Layout changed to `container-fluid` for full-width display
- Map preview column: `col-lg-2`, Hotels grid: `col-lg-10`
- Footer now sticks to bottom of page

#### Hotel Cards
- Removed fake ratings and pricing (Geoapify doesn't provide this data)
- Clean card design with hotel image, name, address, and amenity badges
- Curated Unsplash images for hotel cards

---

### 🔧 Technical Changes

#### Files Modified
- `hotels.html` - Map modal, Leaflet.js integration, layout changes
- `hotels.js` - Map functionality, search improvements, removed filter references
- `index.html` - Date autofill, search redirect, updated datalist
- `HotelsApiService.java` - Added new district coordinates
- `application.properties` - H2 database configuration
- `.gitignore` - Added common exclusions

#### Dependencies Added
- **Leaflet.js 1.9.4** - Interactive maps (CDN)
- Uses **OpenStreetMap** tiles (free, no API key needed)

#### API
- Geoapify Places API for hotel data
- Geoapify Static Maps API for map preview image
- No new API keys required

---

### 📝 Notes for Team

1. **Database**: Using H2 in-memory database (data resets on restart)
2. **Map Feature**: Completely free - uses Leaflet.js + OpenStreetMap
3. **Adding New Districts**: Add coordinates to `CITY_COORDS` map in `HotelsApiService.java`
4. **Hotel Images**: Currently using curated Unsplash URLs; hybrid PostgreSQL system planned

---

### 🐛 Bug Fixes
- Fixed search error caused by referencing deleted filter elements
- Fixed 403 errors for Rangamati and Bandarban (missing coordinates)
- Fixed hotel cards image display consistency

---

## How to Run

```bash
# Start the application
.\mvnw.cmd spring-boot:run

# Access at
http://localhost:8080
```

---

*Updated by: Team Deadlock*
