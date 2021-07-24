@extends('layouts.main')

@section('page')
  <div class="container-fluid py-3">
    <div class="d-flex align-items-center mb-4">
        <h1 class="h3 mb-0 text-gray-800"><i class="fas fa-pen"></i> Ubah Profil</h1>
    </div>

    <form method="POST" action="{{ route('profil') }}" style="text-align: center;">
		@csrf

		<div class="row d-flex flex-row align-items-center">
	      	<div class="col-auto d-flex flex-column justify-content-center align-items-center">
	            <img class="circle-img" src="{{ asset('images/logo_blue.png') }}" alt="Avatar">

	            <a href="#" class="btn btn-transparent" style="color: var(--blue);"><i class="fas fa-camera"></i> Ubah foto</a>
                <input id="avatar" name="avatar" class="btn btn-transparent" style="color: var(--blue);" type="file">
			</div>

			<div class="col d-flex flex-column align-items-start">
                <div>Nama</div>
				<div class="form-group row mb-3 w-100">
                    <div class="col-12">
                        <input id="name" type="text" class="form-control form-control-light @error('name') is-invalid @enderror" name="name" value="{{ Auth::user()->name }}" required autocomplete="name" placeholder="Nama">

                        @error('name')
                            <span class="invalid-feedback" role="alert">
                                <strong>{{ $message }}</strong>
                            </span>
                        @enderror
                    </div>
                </div>

                <div>Username</div>
                <div class="form-group row mb-3 w-100">
                    <div class="col-12">
                        <input id="username" type="text" class="form-control form-control-light @error('username') is-invalid @enderror" name="username" value="{{ Auth::user()->username }}" required autocomplete="username" placeholder="Username">

                        @error('username')
                            <span class="invalid-feedback" role="alert">
                                <strong>{{ $message }}</strong>
                            </span>
                        @enderror
                    </div>
                </div>
			</div>
		</div>

		<div class="row">
			<div class="form-group w-100 my-5">
                <div class="col-12">
                    <button type="submit" class="btn btn-primary shadow-none w-100">
                        Simpan
                    </button>
                </div>
            </div>
		</div>

	</form>
  </div>

@endsection