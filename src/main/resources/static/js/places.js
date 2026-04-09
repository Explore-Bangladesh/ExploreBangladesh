function resolveModalImage(place) {
  const localFallback = 'Assets/green scenary.jpg';

  if (typeof window.resolvePlaceImage === 'function') {
    return window.resolvePlaceImage(place);
  }

  const fallback = (place && place.imageUrl) || localFallback;
  if (typeof window.getLocalPlaceImage === 'function') {
    return window.getLocalPlaceImage(place && place.name, fallback);
  }

  return fallback;
}

// --- MODAL FOR PLACE DETAILS ---
function showDetailsModal(place) {
  // Check if a modal already exists, if not, create it
  let modal = document.getElementById('placeDetailsModal');
  if (!modal) {
    const modalHTML = `
      <div class="modal fade" id="placeDetailsModal" tabindex="-1" aria-labelledby="placeDetailsModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="placeDetailsModalLabel"></h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <div class="row">
                <div class="col-md-6">
                  <img id="modalPlaceImage" src="" class="img-fluid rounded mb-3" alt="Place image">
                  <p id="modalPlaceDescription"></p>
                </div>
                <div class="col-md-6">
                  <h5>Plan Your Visit</h5>
                  <p>Find services available for this location:</p>
                  <div class="d-grid gap-2">
                    <a href="#" id="modalCarLink" class="btn btn-outline-primary"><i class="bi bi-car-front-fill me-2"></i>Find a Car</a>
                    <a href="#" id="modalHotelLink" class="btn btn-outline-primary"><i class="bi bi-building me-2"></i>Find a Hotel</a>
                    <a href="#" id="modalGuideLink" class="btn btn-outline-primary"><i class="bi bi-person-check-fill me-2"></i>Find a Guide</a>
                    <a href="#" id="modalHowToGoLink" class="btn btn-outline-info mt-3" target="_blank"><i class="bi bi-map me-2"></i>How to Go (Google Maps)</a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `;
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    modal = document.getElementById('placeDetailsModal');
  }

  // Populate modal content
  document.getElementById('placeDetailsModalLabel').textContent = place.name;
  const modalImage = document.getElementById('modalPlaceImage');
  modalImage.src = resolveModalImage(place);
  modalImage.alt = `${place.name} image`;
  modalImage.onerror = function onModalImageError() {
    this.onerror = null;
    this.src = 'Assets/green scenary.jpg';
  };
  document.getElementById('modalPlaceDescription').textContent = place.description;

  // Create dynamic links with correct parameter names for each page
  const locationName = place.name.split(',')[0].trim();
  document.getElementById('modalCarLink').href = `cars.html?location=${encodeURIComponent(locationName)}`;
  document.getElementById('modalHotelLink').href = `hotels.html?destination=${encodeURIComponent(locationName)}`;
  document.getElementById('modalGuideLink').href = `guides.html?city=${encodeURIComponent(locationName)}`;
  
  const googleMapsUrl = `https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(place.name)}`;
  document.getElementById('modalHowToGoLink').href = googleMapsUrl;

  // Show the modal
  const bootstrapModal = new bootstrap.Modal(modal);
  bootstrapModal.show();
}
