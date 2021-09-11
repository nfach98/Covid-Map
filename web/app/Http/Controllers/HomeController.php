<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class HomeController extends Controller
{
    public $successStatus = 200;

    public function index()
    {
        $curl = curl_init();

        curl_setopt_array($curl, array(
            CURLOPT_URL => "https://lawancovid-19.surabaya.go.id/area_pasien/load_koordinat_line",
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_ENCODING => "",
            CURLOPT_TIMEOUT => 30000,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
            CURLOPT_CUSTOMREQUEST => "GET",
            CURLOPT_HTTPHEADER => array(
                'Content-Type: application/json',
            ),
        ));
        $response = curl_exec($curl);
        $err = curl_error($curl);
        curl_close($curl);

        if ($err) {
            echo "cURL Error #:" . $err;
        } else {
            $resp = json_decode($response);
            $lines_green = [];
            $lines_yellow = [];
            $lines_red = [];
            $data = [];

            foreach ($resp->line_coordinats as $i => &$line_coordinate) {
                if($resp->data[$i]->color != "" && $resp->data[$i]->status_confirm == "CONFIRM"){
                    $line = [
                        "type" => "Feature",
                        "geometry" => [
                            "type" => "LineString",
                            "coordinates" => $line_coordinate
                        ],
                        "properties" => [
                            "color" => $resp->data[$i]->color,
                            "description" => "<strong>". $resp->data[$i]->nama_jalan ."</strong><p> Jumlah terkonfirmasi: ". $resp->data[$i]->jml_status_confirm ."</p>"
                        ]
                    ];

                    if($resp->data[$i]->jml_status_confirm == "0") {
                        array_push($lines_green, $line);
                    } else if($resp->data[$i]->jml_status_confirm == "1") {
                        array_push($lines_yellow, $line);
                    } else {
                        array_push($lines_red, $line);
                    }

                    // array_push($data, $resp->data[$i]);
                }
            }

            $fc_green = [
                "type" => "FeatureCollection",
                "features" => $lines_green
            ];

            $fc_yellow = [
                "type" => "FeatureCollection",
                "features" => $lines_yellow
            ];

            $fc_red = [
                "type" => "FeatureCollection",
                "features" => $lines_red
            ];
        }

        return view('home', [
            "fc_green" => json_encode($fc_green), 
            "fc_yellow" => json_encode($fc_yellow),
            "fc_red" => json_encode($fc_red),
        ]);
    }

    public function titik(Request $request) {

        $curl = curl_init();

        curl_setopt_array($curl, array(
            CURLOPT_URL => "https://lawancovid-19.surabaya.go.id/area_pasien/load_koordinat_titik",
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_ENCODING => "",
            CURLOPT_TIMEOUT => 30000,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
            CURLOPT_CUSTOMREQUEST => "GET",
            CURLOPT_HTTPHEADER => array(
                'Content-Type: application/json',
            ),
        ));
        $response = curl_exec($curl);
        $err = curl_error($curl);
        curl_close($curl);

        if ($err) {
            echo "cURL Error #:" . $err;
        } else {
            $resp = json_decode($response);
            $dots = [];

            foreach ($resp->line_coordinats as $dots_array) {
                foreach ($dots_array as $dots_array2) {
                    $point = [];
                    $point["type"] = "Feature";
                    $point["geometry"] = [
                        "type" => "Point",
                        "coordinates" =>  $dots_array2
                    ];
                    array_push($dots, $point);
                }
            }

            $fc = [
                "type" => "FeatureCollection",
                "features" => $dots
            ];

            return response()->json($fc, $this->successStatus);
        }
    }

    public function line(Request $request) {

        $curl = curl_init();

        curl_setopt_array($curl, array(
            CURLOPT_URL => "https://lawancovid-19.surabaya.go.id/area_pasien/load_koordinat_line",
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_ENCODING => "",
            CURLOPT_TIMEOUT => 30000,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
            CURLOPT_CUSTOMREQUEST => "GET",
            CURLOPT_HTTPHEADER => array(
                'Content-Type: application/json',
            ),
        ));
        $response = curl_exec($curl);
        $err = curl_error($curl);
        curl_close($curl);

        if ($err) {
            echo "cURL Error #:" . $err;
        } else {
            $resp = json_decode($response);
            $lines = [];
            $data = [];

            foreach ($resp->line_coordinats as $i => &$line_coordinate) {
                if($resp->data[$i]->color != "" && $resp->data[$i]->status_confirm == "CONFIRM"){
                    $nama_jalan = str_replace(",", " ", $resp->data[$i]->nama_jalan);
                    $line = [
                        "type" => "Feature",
                        "geometry" => [
                            "type" => "LineString",
                            "coordinates" => $line_coordinate
                        ],
                        "properties" => [
                            "color" => $resp->data[$i]->color,
                            "description" => $nama_jalan .",". $resp->data[$i]->jml_status_confirm
                        ]
                    ];

                    array_push($lines, $line);
                    array_push($data, $resp->data[$i]);
                }
            }

            $fc = [
                "type" => "FeatureCollection",
                "features" => $lines
            ];

            return response()->json([
                "feature_collection" => json_encode($fc),
                "data" => $data
            ], $this->successStatus);
        }
    }
}
