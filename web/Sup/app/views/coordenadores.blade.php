@extends("index")
@section("main")
@parent
<div class="row">
@foreach($coordinators   as $coordinator)
<div class="col s12 m3">      
<div class="card hoverable">
  <div class="card-image menu">
    <a href={{URL::route('module', ['coordinator'=>$coordinator->id])}}><img src="img/web_hi_res_512.png" height="200" width="100"></a>
    <span class="card-title"></span>
  </div>
  <div class="card-content center">
      <h5>Coordenador {{$coordinator->id}}</h5>
      Número de Módulos: {{$coordinator->numb_of_modules}}
      <br/><i class="zmdi zmdi-battery"></i> {{$coordinator->battery}}%
  </div>
  <div class="card-action center">
    <div class="switch">
      <label>Off
      <input id='{{$coordinator->id}}' class="status-coordinator" type="checkbox" {{$coordinator->status ? 'checked':''}}>
      <span class="lever"></span>On
      </label>
      </div>
  </div>
</div>
</div>          
@endforeach
</div>

<script>
 $(function(){
   
     $(".status-coordinator").change(function(e){
       alert([$(this).attr('id'), $(this).prop('checked')]);
       var status = $(this).prop('checked') ? 1 : 0;
       $.post('/coordinator/switch', {'coordinator':$(this).attr('id'),'status':status}, function(data){
         alert(data.result);
         console.log(data);
       })
        .fail(function(){
          alert("falhou");
        });          
    });
   
  });
</script>
@stop
