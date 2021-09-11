@extends('layouts.main')

@section('script')
    <script type="text/javascript">
      window.onload = function () {
        mapboxgl.accessToken = 'pk.eyJ1IjoibmZhY2g5OCIsImEiOiJja214aGkzd2Uwb3o1MnBtOWR0c3NyMGJvIn0.yIkMVzbHjJBHp7UHH4r4wQ';

        /*$.ajax({
          url: "https://lawancovid-19.surabaya.go.id/area_pasien/load_koordinat_titik",
          type: "GET",
          cache: false,
          dataType: 'json',
          success: function(data){
            var map = new mapboxgl.Map({
              container: 'map',
              style: 'mapbox://styles/mapbox/streets-v11',
              center: [data.longitude, data.latitude],
              zoom: 15
            });

            function getRoute(start, end) {
              var url = 'https://api.mapbox.com/directions/v5/mapbox/cycling/' + start[0] + ',' + start[1] + ';' + end[0] + ',' + end[1] + '?steps=true&geometries=geojson&access_token=' + mapboxgl.accessToken;

              $.ajax({
                url: url,
                type: "GET",
                cache: true,
                dataType: 'json',
                success: function(data) {
                  var route = data.routes[0].geometry.coordinates;
                  console.log(route);
                  var geojson = {
                    type: 'Feature',
                    properties: {},
                    geometry: {
                      type: 'LineString',
                      coordinates: route
                    }
                  };

                  if (map.getSource('route')) {
                    map.getSource('route').setData(geojson);
                  } else {
                    map.addLayer({
                      id: 'route',
                      type: 'line',
                      source: {
                        type: 'geojson',
                        data: {
                          type: 'Feature',
                          properties: {},
                          geometry: {
                            type: 'LineString',
                            coordinates: geojson
                          }
                        }
                      },
                      layout: {
                        'line-join': 'round',
                        'line-cap': 'round'
                      },
                      paint: {
                        'line-color': '#3887be',
                        'line-width': 5,
                        'line-opacity': 0.75
                      }
                    });
                  }
                },
                error: function(XMLHttpRequest, status, error) { 
                  console.log(error);
                }
              });
            }

            map.addControl(new mapboxgl.GeolocateControl({
                positionOptions: {
                  enableHighAccuracy: true
                },
                trackUserLocation: true
              })
            );

            map.addControl(new mapboxgl.NavigationControl());

            map.addControl(new mapboxgl.FullscreenControl());

            map.on('load', function () {
              // getRoute([data.start_longitude, data.start_latitude], [data.end_longitude, data.end_latitude]);

              setInterval( function() {
                $.ajax({
                  url: "/motorcar",
                  type: "GET",
                  data: {
                    _token:'{{ csrf_token() }}',
                    id: 1
                  },
                  cache: false,
                  dataType: 'json',
                  success: function(data){
                    var end = {
                      type: 'FeatureCollection',
                      features: [{
                        type: 'Feature',
                        properties: {},
                        geometry: {
                          type: 'Point',
                          coordinates: [-7.548892, 111.668518]
                        }
                      }]
                    };

                    map.getSource('end').setData(end);

                    // map.getSource('train').setData({
                    //   'type': 'FeatureCollection',
                    //   'features': [
                    //     {
                    //     'type': 'Feature',
                    //     'geometry': {
                    //       'type': 'Point',
                    //       'coordinates': [data.longitude, data.latitude]
                    //       }
                    //     }
                    //   ]
                    // });
     
                    // map.flyTo({
                    //   center: [data.longitude, data.latitude],
                    //   speed: 1
                    // });
                  },
                  error: function(XMLHttpRequest, status, error) { 
                    console.log(error);
                  }
                });
              }, 1000);

              map.addLayer({
                id: 'end',
                type: 'circle',
                source: {
                  type: 'geojson',
                  data: {
                    type: 'FeatureCollection',
                    features: [{
                      type: 'Feature',
                      properties: {},
                      geometry: {
                        type: 'Point',
                        coordinates: [-7.548892, 111.668518]
                      }
                    }]
                  }
                },
                paint: {
                  'circle-radius': 10,
                  'circle-color': '#f30'
                }
              });
              
              map.addSource('train', { 
                type: 'geojson', 
                data: {
                  'type': 'FeatureCollection',
                  'features': [
                    {
                    'type': 'Feature',
                    'geometry': {
                      'type': 'Point',
                      'coordinates': [-77.4144, 25.0759]
                      }
                    }
                  ]
                } 
              });

              map.addLayer({
                'id': 'train',
                'type': 'symbol',
                'source': 'train',
                'layout': {
                  'icon-image': 'rocket-15'
                }
              });
            });
          },
          error: function(XMLHttpRequest, status, error) { 
            console.log(error);
          }
        });*/

        var map = new mapboxgl.Map({
          container: 'map',
          style: 'mapbox://styles/mapbox/streets-v11',
          center: [112.6384115, -7.3101367],
          zoom: 15
        });

        map.addControl(new mapboxgl.GeolocateControl({
            positionOptions: {
              enableHighAccuracy: true
            },
            trackUserLocation: true
          })
        );

        map.addControl(new mapboxgl.NavigationControl());

        map.addControl(new mapboxgl.FullscreenControl());
      }
    </script>
@endsection

@section('page')
    <div class="container-fluid px-0 py-0">
        <div class="w-100" id="map" style='height: calc(100vh - 80px);'></div>
    </div>
@endsection