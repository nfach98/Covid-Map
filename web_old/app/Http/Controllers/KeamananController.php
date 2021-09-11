<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\History;
use Illuminate\Support\Facades\Auth;
use Validator;
use Illuminate\Support\Facades\Hash;
use Session;

class KeamananController extends Controller
{
     public function __construct()
    {
        $this->middleware('auth');
    }

    public function index()
    {
        $histories = History::where('id_user', Auth::user()->id)
        ->orderBy('id', 'DESC')
        ->get();
        return view('keamanan', ['history' => $histories]);
    }

    public function update(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'password' => ['required', 'string', 'min:8', 'confirmed'],
        ]);

        if($validator->fails()){
            return redirect()->back();
        }

        else {
            $query = User::find(Auth::user()->id);
            $query->update([
                'password' => Hash::make($request->password)
            ]);
            Session::flash('success_msg', 'User details updated successfully!');
            return redirect()->back();
        }
    }
}
