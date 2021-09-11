@extends('layouts.main')

@section('script')
	<script type="text/javascript">
	    $("#toggle-password").click(function() {

	      $(this).toggleClass("fa-eye fa-eye-slash");
	      var input = $($(this).attr("toggle"));
	      if ($("#password").attr("type") == "password") {
	        $("#password").attr("type", "text");
	        $(this).css("color", "var(--yellow)");
	      } else {
	        $("#password").attr("type", "password");
	        $(this).css("color", "var(--gray)");
	      }
	    });

	    $("#toggle-password-confirm").click(function() {

	      $(this).toggleClass("fa-eye fa-eye-slash");
	      var input = $($(this).attr("toggle"));
	      if ($("#password-confirm").attr("type") == "password") {
	        $("#password-confirm").attr("type", "text");
	        $(this).css("color", "var(--yellow)");
	      } else {
	        $("#password-confirm").attr("type", "password");
	        $(this).css("color", "var(--gray)");
	      }
	    });
	</script>
@endsection

@section('page')
  <div class="container-fluid py-3">
    <div class="d-flex align-items-center mb-4">
        <h1 class="h3 mb-0 text-gray-800"><i class="fas fa-lock"></i> Keamanan</h1>
    </div>

    <form method="POST" action="{{ route('keamanan') }}">
		@csrf

		<div>Password baru</div>
      	<div class="form-group row mb-3">
            <div class="col-12">
                <input id="password" type="password" class="form-control form-control-light @error('password') is-invalid @enderror" name="password" required autocomplete="current-password"  placeholder="Password" data-toggle="password">

                @error('password')
                    <span class="invalid-feedback" role="alert">
                        <strong>{{ $message }}</strong>
                    </span>
                @enderror

                <span class="fas fa-eye form-icon" id="toggle-password" style="color: var(--gray)"></span>
            </div>
        </div>

        <div>Konfirmasi password baru</div>
        <div class="form-group row mb-5">
            <div class="col-12">
                <input id="password-confirm" type="password" class="form-control form-control-light" name="password_confirmation" placeholder="Konfirmasi Password" required autocomplete="new-password">

                @error('password')
                    <span class="invalid-feedback" role="alert">
                        <strong>{{ $message }}</strong>
                    </span>
                @enderror

                <span class="fas fa-eye form-icon" id="toggle-password-confirm" style="color: var(--gray)"></span>
            </div>
        </div>

		<div class="form-group w-100 my-5">
            <div class="col-12">
                <button type="submit" class="btn btn-primary shadow-none w-100">
                    Simpan
                </button>
            </div>
        </div>
	</form>
  </div>
@endsection