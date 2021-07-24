<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Support\Facades\Auth;
use Validator;

class UserController extends Controller
{
    public $successStatus = 200;

    public function login(Request $request){
        $validator = Validator::make($request->all(), [
            'username' => 'required|string',
            'password' => 'required|string',
        ]);

        if($validator->fails()){
            return response()->json(['error'=>'Unauthorized'], 401);
        }

        else{
            $user = User::where('username', $request->username)->first();

            if(Auth::attempt([
                    'username' => $request->username, 
                    'password' => $request->password, 
                    'api_token' => null
                ]) && is_null($user->api_token)) {
                Auth::login($user);

                $user = Auth::user();
                $user->api_token = $user->createToken('nApp')->accessToken;
                $user->save();

                $success['token'] = $user->api_token;
                $success['name'] =  $user->name;

                return $success;
            } else {
                return response()->json(['error'=>'Unauthorized'], 401);
            }
        }
    }

    public function register(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'name' => 'required',
            'username' => 'required|unique:users,username',
            'password' => 'required',
            'c_password' => 'required|same:password',
        ]);

        if ($validator->fails()) {
            return response()->json(['error'=>$validator->errors()], 401);            
        }

        $input = $request->all();
        $user = User::create($input);
        $user->api_token = $user->createToken('nApp')->accessToken;
        $user->save();
        
        $success['token'] = $user->api_token;
        $success['name'] =  $user->name;

        return $success;
    }

    public function logout(Request $request)
    {
        if($request->header('token')){
            $query = User::select('name', 'username', 'api_token AS token')
            ->where('api_token', $request->header('token'));
            $user = $query->first();

            if($user) {
                $query->update([
                    'api_token' => null
                ]);
                Auth::logout();

                return response()->json(['status' => 'success'], $this->successStatus);
            } else {
                return response()->json(['error' => 'unauthorized'], 401);
            }
        }
        else{
            return response()->json(['error' => 'unauthorized'], 401);
        }
    }

    public function detail(Request $request)
    {
        if($request->header('token')){
            $query = User::select('name', 'username', 'avatar', 'api_token AS token')
            ->where('api_token', $request->header('token'));
            $user = $query->first();

            if($user) {
                return $user;
            } else {
                return response()->json(['error' => 'unauthorized'], 401);
            }
        }
        else{
            return response()->json(['error' => 'unauthorized'], 401);
        }
    }

    public function update(Request $request)
    {
        if($request->header('token')){

            $validator = Validator::make($request->all(), [
                'name' => 'required',
                'username' => 'required',
            ]);

            if ($validator->fails()) {
                return response()->json(['error' => $validator->errors()], 401);            
            }

            $query = User::select('id', 'name', 'username', 'avatar', 'api_token AS token')
            ->where('api_token', $request->header('token'));
            $user = $query->first();

            if($user) {
                $file = $request->file('avatar');
                $upload_dir = 'uploads';
                $filename = "avatar_" . md5($user->id) . "." . $file->getClientOriginalExtension();
                $file->move($upload_dir, $filename);

                $unique = User::select('username')
                    ->where([
                        ['id', "<>", $user->id],
                        ['username', $request->username]
                    ])
                    ->get()
                    ->count();

                if($unique == 0){
                    $query->update([
                        'name' => $request->name,
                        'username' => $request->username,
                        'avatar' => $upload_dir . "/" . $filename
                    ]);

                    return response()->json([
                        'name' => $file->getClientOriginalName(),
                        'extension' => $file->getClientOriginalExtension(),
                        'status' => 'success'
                    ], $this->successStatus);
                }
                return response()->json(['error' => 'unavailable'], 401);
 
            } 
            else {
                return response()->json(['error' => 'unauthorized'], 401);
            }
        }
        else{
            return response()->json(['error' => 'unauthorized'], 401);
        }
    }

    public function check_username(Request $request)
    {
        if($request->header('token')){
            $validator = Validator::make($request->all(), [
                'username' => 'required',
            ]);

            if ($validator->fails()) {
                return response()->json(['error' => $validator->errors()], 401);            
            }

            $query = User::select('id', 'name', 'username', 'avatar', 'api_token AS token')
            ->where('api_token', $request->header('token'));
            $user = $query->first();

            if($user) {
                $unique = User::where('id', '!=', $user->id)
                    ->where('username', $request->username)
                    ->get()
                    ->count();

                if($unique == 0){
                    return response()->json([
                        'status' => 'available',
                        'username' => $request->username
                    ], $this->successStatus);
                }
                return response()->json(['error' => 'unavailable'], $this->successStatus);

            }
            else {
                return response()->json(['error' => 'unauthorized'], 401);
            }
        }
        else{
            return response()->json(['error' => 'unauthorized'], 401);
        }
    }

    /*public function details()
    {
        $user = Auth::user();
        return response()->json(['success' => $user], $this->successStatus);
    }*/
}
