<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;

class MapController extends Controller
{
	public $successStatus = 200;

	public function __construct()
    {
        $this->middleware('auth');
    }

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
			$lines = [];
			$data = [];

			foreach ($resp->line_coordinats as $i => &$line_coordinate) {
				if($resp->data[$i]->color != "" && $resp->data[$i]->status_confirm == "CONFIRM"
					&& $resp->data[$i]->jml_status_confirm != "0"){
					$line = [
						"type" => "Feature",
						"geometry" => [
							"type" => "LineString",
	    					"coordinates" => $line_coordinate
	    				],
	    				"properties" => [
					       	"color" => $resp->data[$i]->color
				      	]
					];

					array_push($lines, $line);
					array_push($data, $resp->data[$i]);
				}
			}

			$fc = [
		    	"feature_collection" => [
					"type" => "FeatureCollection",
	  				"features" => $lines
				],
		    	"data" => $data
		    ];
		}

        return view('peta', ["fc" => $fc]);
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
				if($resp->data[$i]->color != "" && $resp->data[$i]->status_confirm == "CONFIRM"
					&& $resp->data[$i]->jml_status_confirm != "0"){
					$line = [
						"type" => "Feature",
						"geometry" => [
							"type" => "LineString",
	    					"coordinates" => $line_coordinate
	    				],
	    				"properties" => [
					       	"color" => $resp->data[$i]->color
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
