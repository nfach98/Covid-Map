@extends('layouts.main')

@section('script')
	<style>
	.accordion {
	  background-color: #fff;
	  color: #444;
	  cursor: pointer;
	  padding: 18px;
	  width: 100%;
	  border: none;
	  text-align: left;
	  outline: none;
	  font-size: 15px;
	  transition: 0.4s;
	}

	.active, .accordion:hover {
	  background-color: var(--blue); 
	  color: #fff;
	}

	.panel {
	  padding: 0 18px;
	  display: none;
	  background-color: white;
	  overflow: hidden;
	}
	</style>
@endsection

@section('page')
  <div class="container-fluid py-3">
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800"><i class="fas fa-lightbulb"></i> Tips</h1>
    </div>

    <div class="row">
      	<div class="col-12 mb-4">
          	<div class="d-flex flex-column justify-content-center align-items-center">
          		<a href="#" class="accordion"><i class="fas fa-shield-virus"></i> Bagaimana cara mencegah penyebaran Corona Virus Disease (COVID-19)?</a>
				<div class="panel">
					<p>Beberapa cara yang dapat dilakukan untuk pencegahan penularan COVID-19 adalah:</p>
					<ol>
					  	<li>Jaga Kesehatan dan Kebugaran anda agar sistem imunitas atau kekebalan tubuh meningkat.</li>
					  	<li>Cucilah tangan menggunakan sabun dan air mengalir. Karena hal ini dapat membunuh virus yang ada di tangan kita. hal ini murah dan mudah untuk di lakukan, untuk menghindari penularan dari tangan. Jika tidak ada air dan sabun, bisa menggunakan hand-sanitizer berbasis alkohol.</li>
					  	<li>Upaya menerapkan etika batuk dan bersin. Tutup hidung dan mulut anda dengan tisu atau dengan lengan atas bagian dalam.</li>
					  	<li>Jaga jarak saat bertemu dengan orang lain, sekurang-kurangnya 1 meter. Terutama dengan orang yang sedang menderita batuk, pilek, bersin dan demam, Karena saat seseorang terinfeksi penyakit saluran pernafasan seperti COVID-19, batuk atau bersin dapat meghasilkan droplet yang mengandung virus.</li>
					  	<li>Hindari menyentuh mata, hidung dan mulut sebelum mecuci tangan. Karena mata, hidung dan mulut dapat menjadi jalan masuk virus yang hinggap ke tangan yang belum di cuci.</li>
					  	<li>Menggunakan masker penutup hidung dan mulut Ketika anda sakit.</li>
					  	<li>Buang tisu atau masker yang sudah digunakan ke tempat sampah kemudian cucilah tangan anda.</li>
					  	<li>Tunda perjalanan ke daerah yang terpapar dengan virus ini sampai ada informasi lebih lanjut.</li>
					  	<li>Jauhi tempat-tempat keramaian atau berdiam diri di rumah selama waktu yang di tentukan oleh pihak berwenang.</li>
					</ol>
				</div>

				<a href="#" class="accordion"><i class="fas fa-people-arrows"></i> Jaga jarak itu penting!</a>
				<div class="panel">
				  	<p>Bila penyebaran virus terjadi dilokasi tertentu, Tindakan mengurangi kontak antar warga pertama-tama dilakukan di lokasi-lokasi tersebut. Dan tidak langsung di tingkat nasional<br></br>Caranya adalah dengan melakukan hal-hal sebagai berikut: </p>
				  	<ol>
					  	<li>Hindari pertemuan besar (lebih dari 10 orang).</li>
					  	<li>Jaga jarak (1 meter atau lebih) dengan orang lain.</li>
					  	<li>Jangan pergi ke sarana Kesehatan kecuali diperlukan.</li>
					  	<li>Bila anda memiliki anggota keluarga atau kawan di rawat di rumah sakit, Batasi pengunjung terutama bila mereka anak-anak atau kelompok resiko tinggi (lanjut usia dan orang dengan penyakit yang dapat memperberat, Misal: Gangguan Jantung, Diabetes dan Penyakit Kronis lainnya)</li>
					  	<li>Orang beresiko tinggi sebaiknya tetap di rumah dan menghindari pertemuan atau kegiatan lain yang dapat membuatnya terpapar virus, Termasuk melakukan perjalanan.</li>
					  	<li>Beri dukungan para anggota keluarga (yang tidak tinggal di rumah anda) ataupun tetangga yang terinfeksi tanpa harus bertemu langsung, Misalnya melalui telepon atau aplikasi bertukar pesan lainnya.</li>
					  	<li>Ikuti panduan resmi di wilayah anda yang bisa saja merubah rutinitas termasuk kegiatan sekolah atau pekerjaan. </li>
					  	<li>Ikuti perkembangan informasi karena situasi dapat berubah dengan cepat sesuai perkembangan penyakit dan penyebarannya.</li>
					</ol>
				</div>

				<a href="#" class="accordion"><i class="fas fa-head-side-mask"></i> Cara tepat Menggunakan Masker</a>
				<div class="panel">
				  	<ol>
					  	<li>Sebelum memasang masker, Cuci tangan pakai sabun dan air mengalir (minimal 20 detik) atau bila tidak tersedia, Gunakan cairan pembersih tangan (minimal alkohol 60%) </li>
					  	<li>Pasang masker menutupi Hidung, Mulut, sampai Dagu. Pastikan tidak ada sela antara wajah dan masker </li>
					  	<li>Jangan buka tutup masker. Jangan menyentuh masker. Bila tersentuh, cuci tangan pakai sabun dan air mengalir. (minimal 20 detik) atau bila tidak tersedia, gunakan cairan pembersih tangan (minimal alkohol 60%) </li>
					  	<li>Ganti masker yang basah atau lembab dengan masker baru. Masker medis hanya boleh digunakan 1 kali saja. Masker kain boleh digunakan berulangkali, namun harus di cuci terlebih dahulu jika ingin menggunakannya lagi</li>
					  	<li>Untuk membuka masker: lepaskan dari belakang. Jangan sentuh bagian depan masker. Untuk masker 1 kali pakai, buang segera di tempat sampah tertutup atau kantong plastik. Untuk masker kain segera cuci dengan detergen. Untuk memasang masker baru, ikuti poin pertama</li>
					</ol>
				</div>

				<a href="#" class="accordion"><i class="fas fa-hospital"></i> Kapan saya harus memeriksakan diri?</a>
				<div class="panel">
				  	<p>Apabila anda dihubungi oleh petugas Kesehatan sebagai hasil Analisa aplikasi peduli lindungi. Berarti anda punya riwayat kontak dengan penderits COVID-19 positif, PDP, atau ODP. Maka silahkan periksakan diri anda. Selain itu, sesuai dengan prosedur yang sudah ditetapkan oleh pihak berwenang atau pemerintah, anda juga dapat menghubungi 119 ext. 9 atau periksa ke rumah sakit rujukan jika : </p>
				  	<ol>
					  	<li>Pernah berkunjung ke daerah endemis COVID-19 dalam 14 hari terakhir dan mengalami gejala seperti demam diatas 38℃elcius, Batuk , dan Sesak nafas yang tidak kunjung membaik.</li>
					  	<li>Pernah kontak langsung dengan pasien COVID-19 atau punya Riwayat perjalanan ke daerah episntrum penyebaran virus, meski tak menunjukkan gejala, namun bisa saja anda menjadi carier corona virus.</li>
					  	<li>Mengalami gejala ringan – sedang yang mengarah ke COVID-19 diantaranya adalah Demam, Batuk, Sesak nafas yang tak kunjung reda, meski tidak ada riwayar kontak langsung dengan pasien atau berada di daerah episentrum.</li>
					</ol>
				</div>
          	</div>
		</div>
  	</div>
  </div>
@endsection