@extends('layouts.app')

@section('head')
  @yield('script')
@endsection

@section('content')
  <div class="sidebar">
      <ul class="sidebar-nav">
         <!--  <a href="{{ url('/') }}" class="logo" style="color: white;">
              <div class="d-flex flex-column justify-content-center">
                  <img class="mx-auto my-1" src="{{ asset('images/logo_white.png') }}" alt="Logo TDR" style="height: 50%; width: 50%;">
                  <h4 class="link-text" align="center">{{ config('app.name', 'Train Data Recorder') }}</h4>
              </div>
          </a> -->
          <li class="logo">
            <img src="{{ asset('images/logo_blue.png') }}" alt="Logo CovidMap">
          </li>

          <li class="side-item">
            <a href="{{ url('/') }}" class="side-link">
              <i class="fas fa-map-marker-alt"></i>
              <span class="link-text">Peta</span>
            </a>
          </li>

          <li class="side-item">
            <a href="{{ url('/histori') }}" class="side-link">
              <i class="fas fa-history"></i>
              <span class="link-text">Histori</span>
            </a>
          </li>

          <li class="side-item">
            <a href="{{ url('/tips') }}" class="side-link">
              <i class="fas fa-lightbulb"></i>
              <span class="link-text">Tips</span>
            </a>
          </li>
      </ul>
  </div>

  <main>
      <nav class="navbar navbar-expand-md navbar-dark shadow-sm">
          <ul class="navbar-nav ml-auto">
              @guest
                  @if (Route::has('login'))
                      <li class="nav-item">
                          <a class="nav-link" href="{{ route('login') }}">{{ __('Login') }}</a>
                      </li>
                  @endif
                  
                  @if (Route::has('register'))
                      <li class="nav-item">
                          <a class="nav-link" href="{{ route('register') }}">{{ __('Register') }}</a>
                      </li>
                  @endif
              @else
                  <li class="nav-item dropdown">
                      <a id="navbarDropdown" class="nav-link dropdown-toggle" href="#" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" v-pre>
                          @if (is_null(Auth::user()->avatar))
                              <img id="profile-avatar" class="img-profile rounded-circle mr-2" src="{{ asset('images/avatar_person.png') }}" style="background-color: {{ Auth::user()->color }};" 
                              width="32" height="32">
                          @else
                              <img id="profile-avatar" class="img-profile rounded-circle mr-2" src="{{ Auth::user()->avatar }}"
                              width="32" height="32">
                          @endif
                          {{ Auth::user()->name }}
                      </a>

                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdown">
                          <a class="dropdown-item" href="/profil">
                              <i class="fas fa-pen"></i>
                              Ubah Profil
                          </a>

                          <a class="dropdown-item" href="/keamanan">
                              <i class="fas fa-lock"></i>
                              Keamanan
                          </a>

                          <a class="dropdown-item" href="{{ route('logout') }}"
                             onclick="event.preventDefault();
                               document.getElementById('logout-form').submit();" style="color: var(--red);">
                              <i class="fas fa-sign-out-alt"></i>
                              {{ __('Logout') }}
                          </a>

                          <form id="logout-form" action="{{ route('logout') }}" method="POST" class="d-none">
                              @csrf
                          </form>
                      </div>
                  </li>
              @endguest
          </ul>
      </nav>

      @yield('page')
  </main>

  <script>
    var dropdownGroupDisplay = "none";

    var dropdown = document.getElementsByClassName("side-dropdown");
    var i;

    for (i = 0; i < dropdown.length; i++) {
      dropdown[i].addEventListener("click", function() {
        this.classList.toggle("active");
        var dropdownContent = this.nextElementSibling;
        if (dropdownContent.style.display === "block") {
          dropdownGroupDisplay = "none";
          dropdownContent.style.display = "none";
          if (typeof(Storage) !== "undefined") {
            sessionStorage.setItem("dropdownGroupDisplay", dropdownGroupDisplay);
          }
        } 
        else {
          dropdownGroupDisplay = "block";
          dropdownContent.style.display = "block";
          if (typeof(Storage) !== "undefined") {
            sessionStorage.setItem("dropdownGroupDisplay", dropdownGroupDisplay);
          }
        }
      });
    }

    $(".sidebar").mouseleave(function(e) {
      if(window.matchMedia("(min-width: 600px)").matches){
        $(".side-dropdown-group").css("display", "none");
      }
    });

    $(".sidebar").mouseenter(function(e) {
      if(window.matchMedia("(min-width: 600px)").matches){
        $(".side-dropdown-group").css("display", sessionStorage.getItem("dropdownGroupDisplay"));
      }
    });

    var acc = document.getElementsByClassName("accordion");
    var i;

    for (i = 0; i < acc.length; i++) {
      acc[i].addEventListener("click", function() {
        this.classList.toggle("active");
        var panel = this.nextElementSibling;
        if (panel.style.display === "block") {
          panel.style.display = "none";
        } else {
          panel.style.display = "block";
        }
      });
    }
  </script>
@endsection