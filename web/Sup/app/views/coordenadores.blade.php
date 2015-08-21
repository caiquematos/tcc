
<div class="row">
        <div class="col s12 m3">
          @foreach($coordinators   as $coordinator)
          <div class="card hoverable">
            <div class="card-image menu">
              <a href={{URL::to('modulos')}}><img src="img/xbee.png" height="200" width="100"></a>
              <span class="card-title"></span>
            </div>
            <div class="card-content center">
                <h5>Coordenador {{$coordinator->id}}</h5>
                Número de Módulos: 
                <br/><i class="zmdi zmdi-battery"></i> {{$coordinator->battery}}
            </div>
            <div class="card-action center">
              <div class="switch">
                <label>Off
                <input type="checkbox" checked={{$coordinator->status}}>
                <span class="lever"></span>On
                </label>
                </div>
            </div>
          </div>
          @endforeach
        </div>          
</div>

<script>
 $(document).ready(function(){
     $(".menu a").click(function(e){
                        e.preventDefault();
                        var href = $( this ).attr('href');
                        $("#main").load(href);
    });
  });
</script>
