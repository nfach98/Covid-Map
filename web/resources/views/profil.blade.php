@extends('layouts.main')

@section('script')
    <style type="text/css">
        #img-label {
            display: none;
        }
    </style>

    <script>
      var loadFile = function(event) {
        var image = document.getElementById('img-avatar');
        var label = document.getElementById('img-label');

        image.src = URL.createObjectURL(event.target.files[0]);
        label.style.display = "inline-block";
        label.innerHTML = event.target.files[0].name;
        console.log(event.target.files[0]);

        image.onload = function() {
          URL.revokeObjectURL(image.src)
        }
      };
    </script>
@endsection

@section('page')
  <div class="container-fluid py-3">
    <div class="d-flex align-items-center mb-4">
        <h1 class="h3 mb-0 text-gray-800"><i class="fas fa-pen"></i> Ubah Profil</h1>
    </div>

    <form method="POST" action="{{ route('profil') }}" style="text-align: center;" enctype="multipart/form-data">
		@csrf

		<div class="row d-flex flex-row align-items-center">
	      	<div class="col-auto d-flex flex-column justify-content-center align-items-center">
	            <img id="img-avatar" class="circle-img" width="512" height="512" src="{{ asset(Auth::user()->avatar) }}" alt="Avatar">

                <div class="form-group row mb-3">
                    <div class="upload-btn-wrapper d-flex flex-column">
                      <button class="btn btn-secondary"><i class="fas fa-camera"></i> Ubah foto</button>
                      <label id="img-label">AA</label>
                      <input id="avatar" name="avatar" type="file" accept="image/*" onchange="loadFile(event)"/>
                    </div>
                </div>

	            <!-- <a href="#" class="btn btn-transparent" style="color: var(--blue);"><i class="fas fa-camera"></i> Ubah foto</a>
                <input id="avatar" name="avatar" class="btn btn-transparent" style="color: var(--blue);" type="file"> -->
			</div>

			<div class="col d-flex flex-column align-items-start">
				<div class="form-group row mb-3 w-100">
                    <label>Nama</label>
                    <input id="name" type="text" class="form-control form-control-light @error('name') is-invalid @enderror" name="name" value="{{ Auth::user()->name }}" required autocomplete="name" placeholder="Nama">

                    @error('name')
                        <span class="invalid-feedback" role="alert">
                            <strong>{{ $message }}</strong>
                        </span>
                    @enderror
                </div>

                <div class="form-group row mb-3 w-100">
                    <label>Username</label>
                    <input id="username" type="text" class="form-control form-control-light @error('username') is-invalid @enderror" name="username" value="{{ Auth::user()->username }}" required autocomplete="username" placeholder="Username">

                    @error('username')
                        <span class="invalid-feedback" role="alert">
                            <strong>{{ $message }}</strong>
                        </span>
                    @enderror
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