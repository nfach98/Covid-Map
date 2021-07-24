<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\History;
use Illuminate\Support\Facades\Auth;
use Validator;

class HistoryController extends Controller
{
    public $successStatus = 200;

    public function __construct()
    {
        $this->middleware('auth');
    }

    public function index()
    {
        $histories = History::where('id_user', Auth::user()->id)
        ->orderBy('id', 'DESC')
        ->get();
        return view('history', ['history' => $histories]);
    }

    public function add(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'from' => 'required',
            'to' => 'required'
        ]);

        if ($validator->fails()) {
            return response()->json(['error'=>$validator->errors()], 401);            
        }

        if($request->header('token')){
            $query = User::select('name', 'username', 'avatar', 'api_token AS token')
            ->where('api_token', $request->header('token'));
            $user = $query->first();

            if($user) {
                $history = History::insert([
	                'location_from' => $request->from,
	                'location_to' => $request->to,
	                'datetime' => date('Y-m-d H:i:s'),
	                'id_user' => $user->id
	            ]);

	            return $history;
            } else {
                return response()->json(['error' => 'unauthorized'], 401);
            }
        }
        else{
            return response()->json(['error' => 'unauthorized'], 401);
        }
    }

    public function remove(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'id' => 'required'
        ]);

        if ($validator->fails()) {
            return response()->json(['error'=>$validator->errors()], 401);            
        }

        if($request->header('token')){
            $query = User::select('name', 'username', 'avatar', 'api_token AS token')
            ->where('api_token', $request->header('token'));
            $user = $query->first();

            if($user) {
               $history = History::where('id', $request->id)
               ->where('id_user', $user->id)
               ->delete();
	           return $status;
            } else {
                return response()->json(['error' => 'unauthorized'], 401);
            }
        }
        else{
            return response()->json(['error' => 'unauthorized'], 401);
        }
    }

    public function get(Request $request)
    {
        if($request->header('token')){
            $query = User::select('name', 'username', 'avatar', 'api_token AS token')
            ->where('api_token', $request->header('token'));
            $user = $query->first();

            if($user) {
            	return History::where('id_user', $user->id)
                ->orderBy('id', 'DESC')
                ->get();
            } else {
                return response()->json(['error' => 'unauthorized'], 401);
            }
        }
        else{
            return response()->json(['error' => 'unauthorized'], 401);
        }
    }
}
