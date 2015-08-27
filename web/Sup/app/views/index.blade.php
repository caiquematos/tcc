<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0"/>
  <title>Supervisório Xbee</title>

  <!-- CSS  -->
  <link href="css/material-design-iconic-font.css" rel="stylesheet">
  <link href="css/materialize.css" type="text/css" rel="stylesheet" media="screen,projection"/>
  <link href="css/style.css" type="text/css" rel="stylesheet" media="screen,projection"/>
    
      <!--  Scripts-->
  <script src="js/jquery-2.1.1.min.js"></script>
  <script src="js/materialize.js"></script>
  <script src="js/init.js"></script>
    
</head>
<body>
  <nav class=" #b2dfdb teal lighten-4" role="navigation">
    <div class="nav-wrapper container"><a id="logo-container" href="#" class="brand-logo">Supervisório Xbee</a>
      <ul class="right hide-on-med-and-down">
        @if(Session::has('user'))
        <li class="menu"><a href={{URL::to('coordinator/web-list')}}>Coordenadores</a></li>
        <li class="menu"><a href={{URL::to('logout')}}>Sair</a></li>
        @endif
      </ul> 
      <ul id="nav-mobile" class=" side-nav">
        <li><a href="#"></a></li>
      </ul>
      <a href="#" data-activates="nav-mobile" class="button-collapse"><i class="zmdi zmdi-menu"></i></a>
    </div>
  </nav>

  <div id="main" style="position:relative;top:30px"> 
    @section("main")
    @show
  </div>
  
  <footer class="page-footer #4db6ac teal lighten-2">
    <div class="container">
      <div class="row">
        <div class="col l6 s12">
          <h5 class="white-text">Company Bio</h5>
          <p class="grey-text text-lighten-4">Supervisório de Rede Sem Fio baseada em módulos Xbees.</p>

        </div>
      </div>
    </div>
    <div class="footer-copyright">
      <div class="container">
      Made by <a class="orange-text text-lighten-3" >Caíque Matos</a>
      </div>
    </div>
  </footer>

<script>
 $(document).ready(function(){
    // the "href" attribute of .modal-trigger must specify the modal ID that wants to be triggered
    $('.modal-trigger').leanModal();
     $(".menu a").click(function(e){
                        e.preventDefault();
                        var href = $( this ).attr('href');
                        $("#main").load(href);
    });
  });
</script>
    

  </body>
</html>
