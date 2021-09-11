@extends('layouts.main')

@section('script')
    <style type="text/css">
      strong{
        color: var(--blue);
      }

      .geocoder {
        position: absolute;
        z-index: 1;
        width: calc(70% - 5rem);
      }

      #search-from, #search-to {
        width: 40%;
      }

      .mapboxgl-ctrl-geocoder {
        min-width: 100%;
      }

      /*#search-from {
        position: absolute;
        margin: 2em;
        width: 100%;
        top: 0;
        bottom: 0;
        z-index: 100;
        background-color: rgba(255, 255, 255, 0);
        overflow-y: scroll;
        font-family: sans-serif;
        font-size: 0.8em;
        line-height: 2em;
      }

      .mapboxgl-ctrl-geocoder .mapboxgl-ctrl {
        width: 100%;
      }*/
    </style>

    <script type="text/javascript">
      window.onload = function () {
        mapboxgl.accessToken = 'pk.eyJ1IjoibmZhY2g5OCIsImEiOiJja214aGkzd2Uwb3o1MnBtOWR0c3NyMGJvIn0.yIkMVzbHjJBHp7UHH4r4wQ';

        function getRoute(start, end) {
          var url = 'https://api.mapbox.com/directions/v5/mapbox/cycling/' + start[0] + ',' + start[1] + ';' + end[0] + ',' + end[1] + '?steps=true&geometries=geojson&access_token=' + mapboxgl.accessToken;

          var req = new XMLHttpRequest();
          req.open('GET', url, true);
          req.onload = function() {
            var json = JSON.parse(req.response);
            var data = json.routes[0];
            var route = data.geometry.coordinates;
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
                  'line-width': 8,
                  'line-opacity': 0.75
                }
              });
            }
          };
          req.send();
        }

        var mapClick = function (e) {
          var coordinates = e.features[0].geometry.coordinates.slice();
          var description = e.features[0].properties.description;
          
          while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
            coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
          }

          var middle = 0;
          if(coordinates.length > 2){
            middle = Math.floor(coordinates.length / 2);
          }
           
          new mapboxgl.Popup()
          .setLngLat(coordinates[middle])
          .setHTML(description)
          .addTo(map);
        };
        var mouseEnter = function () {
          map.getCanvas().style.cursor = 'pointer';
        };
        var mouseLeave = function () {
          map.getCanvas().style.cursor = '';
        }  

        var geojsonGreen = {!! $fc_green !!};
        var geojsonYellow = {!! $fc_yellow !!};
        var geojsonRed = {!! $fc_red !!};

        var start = [];
        var end = [];

        var map = new mapboxgl.Map({
          container: 'map',
          style: 'mapbox://styles/mapbox/streets-v11',
          center: [112.772128, -7.207624],
          zoom: 16
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

        var geocoderFrom = new MapboxGeocoder({
          accessToken: mapboxgl.accessToken,
          mapboxgl: mapboxgl,
          placeholder: "Titik awal"
        });

        var geocoderTo = new MapboxGeocoder({
          accessToken: mapboxgl.accessToken,
          mapboxgl: mapboxgl,
          placeholder: "Tujuan"
        });
        
        geocoderFrom.addTo('#search-from');
        geocoderTo.addTo('#search-to');

        map.on('load', function () {
          //green
          map.addSource('confirmed-green', {
            'type': 'geojson',
            'data': geojsonGreen
          });

          map.addLayer({
            'id': 'confirmed-green',
            'type': 'line',
            'source': 'confirmed-green',
            'layout': {
              'line-join': 'round',
              'line-cap': 'round'
            },
            'paint': {
              'line-color': '#0be25c',
              'line-width': 8,
              'line-opacity': 0.75
            }
          });

          //yellow
          map.addSource('confirmed-yellow', {
            'type': 'geojson',
            'data': geojsonYellow
          });

          map.addLayer({
            'id': 'confirmed-yellow',
            'type': 'line',
            'source': 'confirmed-yellow',
            'layout': {
              'line-join': 'round',
              'line-cap': 'round'
            },
            'paint': {
              'line-color': '#fee300',
              'line-width': 8,
              'line-opacity': 0.75
            }
          });

          //red
          map.addSource('confirmed-red', {
            'type': 'geojson',
            'data': geojsonRed
          });

          map.addLayer({
            'id': 'confirmed-red',
            'type': 'line',
            'source': 'confirmed-red',
            'layout': {
              'line-join': 'round',
              'line-cap': 'round'
            },
            'paint': {
              'line-color': '#ff0000',
              'line-width': 8,
              'line-opacity': 0.75
            }
          });

          map.addSource('from-point', {
            'type': 'geojson',
            'data': {
              'type': 'FeatureCollection',
              'features': []
            }
          });
          map.addSource('to-point', {
            'type': 'geojson',
            'data': {
              'type': 'FeatureCollection',
              'features': []
            }
          });
           
          map.addLayer({
            'id': 'from-point',
            'source': 'from-point',
            'type': 'circle',
            'paint': {
              'circle-radius': 8,
              'circle-color': '#0A42A6',
              'circle-stroke-color': '#FFD70E',
              'circle-stroke-width': 2,
            }
          });
          map.addLayer({
            'id': 'to-point',
            'source': 'to-point',
            'type': 'circle',
            'paint': {
              'circle-radius': 8,
              'circle-color': '#FFD70E',
              'circle-stroke-color': '#0A42A6',
              'circle-stroke-width': 2,
            }
          });

          map.on('click', 'confirmed-green', mapClick);
          map.on('click', 'confirmed-yellow', mapClick);
          map.on('click', 'confirmed-red', mapClick);
          
          map.on('mouseenter', 'confirmed-green', mouseEnter);
          map.on('mouseenter', 'confirmed-yellow', mouseEnter);
          map.on('mouseenter', 'confirmed-red', mouseEnter);

          map.on('mouseleave', 'confirmed-green', mouseLeave);
          map.on('mouseleave', 'confirmed-yellow', mouseLeave);
          map.on('mouseleave', 'confirmed-red', mouseLeave);

          geocoderFrom.on('result', function (e) {
            start = e.result.center;
            map.getSource('from-point').setData(e.result.geometry);
            map.flyTo({
              center: e.result.center,
              speed: 1,
              zoom: 16
            });

            if(start.length > 0 && end.length > 0){
              getRoute(start, end);
            }
          });

          geocoderFrom.on('clear', function () {
            map.getSource('from-point').setData({
              'type': 'FeatureCollection',
              'features': []
            });
          });

          geocoderTo.on('result', function (e) {
            end = e.result.center;
            map.getSource('to-point').setData(e.result.geometry);
            map.flyTo({
              center: e.result.center,
              speed: 1,
              zoom: 16
            });

            if(start.length > 0 && end.length > 0){
              getRoute(start, end);
            }
          });

          geocoderTo.on('clear', function () {
            map.getSource('to-point').setData({
              'type': 'FeatureCollection',
              'features': []
            });
          });
        });
      }
    </script>
@endsection

@section('page')
    <div class="container-fluid px-0 py-0">
        <div class="w-100" id="map" style='height: calc(100vh - 80px);'>
            <div class="row no-gutters geocoder px-2 py-2">
                <div id="search-from"></div>

                <div id="search-to" class="ml-2"></div>
            </div>
        </div>
    </div>
@endsection