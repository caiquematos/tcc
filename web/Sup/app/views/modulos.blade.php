      <div class="row">
        <div class="col s12 m3">
          <div class="card hoverable">
            <div class="card-image">
              <a href="#modalAmostras1" class="modal-trigger"><img src="img/xbee.png" height="200" width="100"></a>
              <span class="card-title"></span>
            </div>
            <div class="card-content center">
                <h5>Módulo 01</h5>
                Número de Amostras: 02
                <br/><i class="zmdi zmdi-battery"></i> 33%
            </div>
            <div class="card-action row center">
                <div class="col s6"><div class="switch">
                <label>Off<input type="checkbox">
                <span class="lever"></span>On</label>
                </div></div>
                <div class="col s6">
                <i style="cursor:pointer" class="zmdi zmdi-menu zmdi-hc-2x right activator"></i></div>
            </div>
                <div class="card-reveal">
      <span class="card-title grey-text text-darken-4">Detalhes<i class="zmdi zmdi-close right"></i></span><br/>
        Coordenador<br/> 01             
      <hr>
        Frequência de pacotes<input type="number" value="17.0">              
      
        Sleep Time<input type="time" value="17:56:00">           
      
        Sleep Frequency<input type="number" value="2">             
      
        Número de Amostras<br/><input type="number" value="4">
                    
        <div class="center" ><a class="btn waves-effect waves-light green">Alterar</a></div>
                    
        </div>
          </div>
        </div>          
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
 $(document).ready(function(){
    $('.modal-trigger').leanModal();

  });
</script>
