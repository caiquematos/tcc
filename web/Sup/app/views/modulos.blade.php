<div class="row">
  @foreach($modules as $module)
  <div class="col s12 m3">
      <div class="card hoverable">
        <div class="card-image">
          <a href="#modalAmostra" class="modal-trigger" module='{{$module->id}}'><img src="img/web_hi_res_512.png" height="200" width="100"></a>
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
    <form action="/module/edit">
      <input hidden name="module" value="{{$module->id}}">
    <hr>  
    Frequência de pacotes<input type="number" name="packFrequency" value="{{$module->packFrequency}}">              

    Sleep Time<input type="time" name="sleepTime" value="{{$module->sleepTime}}">           

    Sleep Frequency<input type="number" name="sleepFrequency" value="{{$module->sleepFrequency}}">             

    Número de Amostras<br/> {{$module->numb_of_samples}}

    <div class="center" ><button class="btn waves-effect waves-light green" type="submit" name="action">Alterar</a></div>
    </form>
    </div>
      </div>
  </div>  
  @endforeach
  
  <!-- Modal Structure -->
<div id="modalAmostra" class="modal">
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
   

    $(".modal-trigger").click(function(e){
      var module = $(this).attr('module');
      $("#modalAmostra").load("/module/web-sample",
                              {"module":module},
                              function(){
                                            $(".modal-trigger").leanModal();

      });
    });
   
  });
</script>