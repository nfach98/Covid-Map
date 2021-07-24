@extends('layouts.app')

@section('head')
<style type="text/css">
    html, body {
        height: 100%;
        margin: 0;
    }

    #app{
        height: 100vh;
    }

    .background {
        position: fixed; 
        top: 0; 
        left: 0; 
        width: 100vw; 
        height: 100vh;
        z-index: -1;
    }

    .background img {
        position: absolute; 
        top: 0; 
        left: 0; 
        right: 0; 
        bottom: 0; 
        margin: auto; 
        min-width: 50%;
        min-height: 50%;
    }

    .form-icon {
        float: right;
        margin-left: -25px;
        margin-right: .75rem;
        margin-top: -1.65rem;
        position: relative;
        z-index: 2;
    }

    #app {
        background-image: linear-gradient(45deg, var(--blue), var(--blue-dark));
    }
</style>
@endsection

@section('content')
<div id="app">
    <div class="container h-100">
        <div class="row d-flex h-100">
            <div class="col-md-4 my-auto mx-auto">
                <div class="col my-3 mx-auto">
                    <h3 align="center" style="font-weight: 600; color: var(--yellow);">Bergabung dengan {{config('app.name')}}</h3>
                </div>

                <form method="POST" action="{{ route('register') }}" style="text-align: center;">
                    @csrf

                    <div class="form-group row mb-3">
                        <div class="col-12">
                            <input id="name" type="text" class="form-control form-control-dark @error('name') is-invalid @enderror" name="name" value="{{ old('name') }}" required autocomplete="name" placeholder="Nama">

                            @error('name')
                                <span class="invalid-feedback" role="alert">
                                    <strong>{{ $message }}</strong>
                                </span>
                            @enderror
                        </div>
                    </div>

                    <div class="form-group row mb-3">
                        <div class="col-12">
                            <input id="username" type="text" class="form-control form-control-dark @error('username') is-invalid @enderror" name="username" value="{{ old('username') }}" required autocomplete="username" placeholder="Username">

                            @error('username')
                                <span class="invalid-feedback" role="alert">
                                    <strong>{{ $message }}</strong>
                                </span>
                            @enderror
                        </div>
                    </div>

                    <div class="form-group row mb-3">
                        <div class="col-12">
                            <input id="password" type="password" class="form-control form-control-dark @error('password') is-invalid @enderror" name="password" required autocomplete="current-password"  placeholder="Password" data-toggle="password">

                            @error('password')
                                <span class="invalid-feedback" role="alert">
                                    <strong>{{ $message }}</strong>
                                </span>
                            @enderror

                            <span class="fas fa-eye form-icon" id="toggle-password" style="color: var(--gray)"></span>
                        </div>
                    </div>

                    <div class="form-group row mb-5">
                        <div class="col-12">
                            <input id="password-confirm" type="password" class="form-control form-control-dark" name="password_confirmation" placeholder="Konfirmasi Password" required autocomplete="new-password">

                            @error('password')
                                <span class="invalid-feedback" role="alert">
                                    <strong>{{ $message }}</strong>
                                </span>
                            @enderror

                            <span class="fas fa-eye form-icon" id="toggle-password-confirm" style="color: var(--gray)"></span>
                        </div>
                    </div>

                    <div class="form-group row mb-3">
                        <div class="col-12">
                            <button type="submit" class="btn btn-primary shadow-none w-100">
                                {{ __('sign.register') }}
                            </button>
                        </div>
                    </div>

                    <div class="form-group mb-5">
                        <p class="text-white" align="center">{{ __('sign.alr_have_acc') }} <a class="text-light" href="{{ url('/login') }}" style="font-weight: 600;">{{ __('sign.login') }}</a></p>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

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