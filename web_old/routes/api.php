<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});

Route::post('login', [App\Http\Controllers\UserController::class, 'login']);
Route::post('register', [App\Http\Controllers\UserController::class, 'register']);
Route::post('logout', [App\Http\Controllers\UserController::class, 'logout']);
Route::post('detail', [App\Http\Controllers\UserController::class, 'detail']);
Route::post('update', [App\Http\Controllers\UserController::class, 'update']);
Route::post('check-username', [App\Http\Controllers\UserController::class, 'check_username']);

Route::post('koord-titik', [App\Http\Controllers\MapController::class, 'titik']);
Route::post('koord-line', [App\Http\Controllers\MapController::class, 'line']);

Route::post('add-history', [App\Http\Controllers\HistoryController::class, 'add']);
Route::post('remove-history', [App\Http\Controllers\HistoryController::class, 'remove']);
Route::post('get-history', [App\Http\Controllers\HistoryController::class, 'get']);

/*Route::group(['middleware' => 'auth:api'], function(){
	Route::post('details', [App\Http\Controllers\UserController::class, 'details']);
});*/
