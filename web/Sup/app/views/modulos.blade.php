<div class="row">
  @foreach($modules as $module)
  <div class="col s12 m3">
      <div class="card hoverable">
        <div class="card-image">
          <a href="#modalAmostras1" class="modal-trigger"><img src="img/xbee.png" height="200" width="100"></a>
          <span class="card-title"></span>
        </div>
        <div class="card-content center">
            <h5>Módulo {{$module->id}}</h5>
            Número de Amostras: {{$module->numb_of_samples}}
            <br/><i class="zmdi zmdi-battery"></i> {{$module->battery}}%
        </div>
        <div class="card-action row center">
            <div class="col s6">
              <div class="switch">
                <label>Off<input id='{{$module->id}}' class="status-module" type="checkbox" {{$module->status ? 'checked':''}}>
                <span class="lever"></span>On</label>
              </div>
            </div>
            <div class="col s6">
            <i style="cursor:pointer" class="zmdi zmdi-menu zmdi-hc-2x right activator"></i></div>
        </div>
            <div class="card-reveal">
    <span class="card-title grey-text text-darken-4">Detalhes<i class="zmdi zmdi-close right"></i></span><br/>
      Coordenador<br/> {{$module->coordinator}}             
    <hr>
    Frequência de pacotes<input type="number" value="{{$module->packFrequency}}">              

    Sleep Time<input type="time" value="{{$module->sleepTime}}">           

    Sleep Frequency<input type="number" value="{{$module->sleepFrequency}}">             

    Número de Amostras<br/><input type="number" value="{{$module->numb_of_samples}}">

    <div class="center" ><a class="btn waves-effect waves-light green">Alterar</a></div>

    </div>
      </div>
  </div>  
  @endforeach
</div>

<!-- Modal Structure -->
<div id="modalAmostras1" class="modal">
    <h5 class="center">Módulo 01</h5>
  <div class="modal-content">
    <ul class="collection">
    <li class="collection-item">0) <b style="font-size:20px">33.33</b> em 2015-08-11 21:25:98.0</li>
    <li class="collection-item">1) <b style="font-size:20px">33.33</b>  em 2015-08-11 21:25:98.0</li>
    <li class="collection-item">2) <b style="font-size:20px">33.33</b>  em 2015-08-11 21:25:98.0</li>
    <li class="collection-item">3) <b style="font-size:20px">33.33</b>  em 2015-08-11 21:25:98.0</li>
      </ul>
  </div>

</div>

<script>
 $(function(){
   
     $(".status-module").change(function(e){
       alert([$(this).attr('id'), $(this).prop('checked')]);
       var status = $(this).prop('checked') ? 1 : 0;
       $.post('/module/status', {'module':$(this).attr('id'),'status':status}, function(data){
         alert(data.result);
         console.log(data);
       })
        .fail(function(){
          alert("falhou");
        });          
    });
   
  });
</script>