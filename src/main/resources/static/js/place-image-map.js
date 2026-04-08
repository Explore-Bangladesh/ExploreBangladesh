window.LOCAL_PLACE_IMAGE_MAP = {
  'Lalbagh Fort': 'Assets/places/lalbagh-fort.jpg',
  'Ahsan Manzil': 'Assets/places/ahsan-manzil.jpg',
  'Ahsan Manzil (Pink Palace)': 'Assets/places/ahsan-manzil.jpg',
  'Pink Palace': 'Assets/places/ahsan-manzil.jpg',
  'National Parliament House': 'Assets/places/parliament-house.jpg',
  'Jatiya Sangsad Bhaban': 'Assets/places/parliament-house.jpg',
  'Shaheed Minar': 'Assets/places/shaheed-minar.jpg',
  'Central Shaheed Minar': 'Assets/places/shaheed-minar.jpg',
  'Liberation War Museum': 'Assets/places/liberation-war-museum.jpg',
  'Liberation war museum': 'Assets/places/liberation-war-museum.jpg',
  'Star Mosque': 'Assets/places/star-mosque.jpg',
  'Star Mosque (Tara Masjid)': 'Assets/places/star-mosque.jpg',
  'Tara Masjid': 'Assets/places/star-mosque.jpg',
  'National Museum of Bangladesh': 'Assets/places/bangladesh-national-museum.jpg',
  'Bangladesh National Museum': 'Assets/places/bangladesh-national-museum.jpg',
  'Atia Mosque': 'Assets/places/atia-mosque.jpg',
  'Madhupur National Park': 'Assets/places/madhupur-national-park.jpg',
  'Jamuna Bridge Corridor': 'Assets/places/jamuna-bridge-tangail.jpg',
  "Cox's Bazar Sea Beach": 'Assets/places/coxs-bazar-sea-beach.jpg',
  "Cox's Bazar Beach": 'Assets/places/coxs-bazar-sea-beach.jpg',
  'Inani Beach': 'Assets/places/inani-beach.jpg',
  'Himchari National Park': 'Assets/places/himchari-national-park.jpg',
  'Ratargul Swamp Forest': 'Assets/places/ratargul-swamp-forest.jpg',
  'Jaflong': 'Assets/places/jaflong.jpg',
  'Lalakhal': 'Assets/places/lalakhal.png',
  'Kaptai Lake': 'Assets/places/kaptai-lake.jpg',
  'Hanging Bridge': 'Assets/places/hanging-bridge-of-rangamati.jpg',
  'Hanging Bridge of Rangamati': 'Assets/places/hanging-bridge-of-rangamati.jpg',
  'Rangamati Hanging Bridge': 'Assets/places/hanging-bridge-of-rangamati.jpg',
  'Shuvolong Waterfall': 'Assets/places/shuvolong-waterfall.jpg',
  'Nilgiri': 'Assets/places/nilgiri-bandarban.jpg',
  'Nilgiri Hill Resort': 'Assets/places/nilgiri-bandarban.jpg',
  'Buddha Dhatu Jadi': 'Assets/places/buddha-dhatu-jadi.jpg',
  'Golden Temple (Buddha Dhatu Jadi)': 'Assets/places/buddha-dhatu-jadi.jpg',
  'Golden Temple': 'Assets/places/buddha-dhatu-jadi.jpg',
  'Nafakhum Waterfall': 'Assets/places/nafakhum-waterfall.jpg',
  'Boga Lake': 'Assets/places/boga-lake.jpg',
  'Rajban Vihara': 'Assets/places/rajban-vihara.jpg'
};

window.getLocalPlaceImage = function(placeOrName, fallback) {
  const name = typeof placeOrName === 'string' ? placeOrName : (placeOrName && placeOrName.name) || '';
  const normalized = String(name).trim();
  return window.LOCAL_PLACE_IMAGE_MAP[normalized] || fallback || 'Assets/green scenary.jpg';
};
