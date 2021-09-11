<?php

use Illuminate\Support\Facades\Route;

Route::group(['middleware' => 'auth'], function()
{
    Route::get('/', [App\Http\Controllers\HomeController::class, 'index']);

	Route::get('/peta', [App\Http\Controllers\MapController::class, 'index']);

	Route::get('/histori', [App\Http\Controllers\HistoryController::class, 'index']);

	Route::get('/tips', [App\Http\Controllers\TipsController::class, 'index']);

	Route::get('/profil', [App\Http\Controllers\ProfilController::class, 'index']);
	Route::post('/profil', [App\Http\Controllers\ProfilController::class, 'update'])->name('profil');

	Route::get('/keamanan', [App\Http\Controllers\KeamananController::class, 'index']);
	Route::post('/keamanan', [App\Http\Controllers\KeamananController::class, 'update'])->name('keamanan');

	Route::get('/home', [App\Http\Controllers\HomeController::class, 'index'])->name('home');
});


Auth::routes();