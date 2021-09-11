<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\History;
use Illuminate\Support\Facades\Auth;
use Validator;
use Session;

class ProfilController extends Controller
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
        return view('profil', ['history' => $histories]);
    }

    public function update(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'name' => ['required', 'string', 'max:255'],
            'username' => ['required', 'string', 'max:40']
        ]);

        if($validator->fails()){
            return redirect()->back();
        }

        else {
            $query = User::find(Auth::user()->id);
            $unique = User::select('username')
                ->where([
                    ['id', "<>", $query->id],
                    ['username', $request->username]
                ])
                ->get()
                ->count();

            if($unique == 0){
                $file = $request->file('avatar');
                $upload_dir = 'uploads';
                $filename = "avatar_" . md5($query->id) . "." . $file->getClientOriginalExtension();
                $file->move($upload_dir, $filename);

                $query->update([
                    'name' => $request->name,
                    'username' => $request->username,
                    'avatar' => $upload_dir . "/" . $filename
                ]);
                Session::flash('success_msg', 'User details updated successfully!');
                return redirect()->back()->with('success', 'Berhasil mengubah profil');
            }
            else {
                return redirect()->back()->withErrors(['message', 'Gagal mengubah profil']);;
            }
        }
    }
}
