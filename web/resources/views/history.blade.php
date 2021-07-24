@extends('layouts.main')

@section('page')
  <div class="container-fluid py-3">
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800"><i class="fas fa-history"></i> Histori</h1>
    </div>

    @if($history->count() > 0)
	    @foreach ($history as $hist)
			<div class="row">
	          	<div class="col-12 mb-4">
	              	<div class="row no-gutters align-items-top">
	                  	<div class="col mr-5">
	                      	<div class="row mx-0" style="color: var(--blue);">
		                        <div class="col-auto">
		                          	<i class="far fa-compass"></i>
		                        </div>
		                        <div class="col-4 text-left">{{ $hist->location_from }}</div>
	                      	</div>
	                      	<div class="row mx-0" style="color: var(--blue);">
		                        <div class="col-auto">
		                          	<i class="fas fa-map-marker-alt"></i>
		                        </div>
		                        <div class="col-4 text-left">{{ $hist->location_to }}</div>
	                      	</div>

	                      	<div class="text-xs my-1" style="color: gray;">{{ date_format(
	                      		date_create($hist->datetime),"d F Y, H:i:s"
	                      	) }}</div>

	                      	<div class="my-2" style="background-color: var(--yellow); height: 2px;"></div>
	                  	</div>
	              	</div>
	  			</div>
	      	</div>
		@endforeach
	@else
	    <div class="row">
          	<div class="col-12 mb-4">
              	<div class="d-flex flex-column justify-content-center align-items-center">
              		<img class="mx-auto my-auto" src="{{ asset('images/image_empty.jpg') }}" alt="Empty" style="height: 25%; width: 25%;">

              		<p class="my-3 font-weight-bold">Belum ada histori</p>
              	</div>
  			</div>
      	</div>
	@endif
  </div>

@endsection