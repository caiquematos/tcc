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
        <li class="menu"><a href={{URL::to('coordenadores')}}>Coordenadores</a></li>
      </ul>

      <ul id="nav-mobile" class="side-nav">
        <li><a href="#"></a></li>
      </ul>
      <a href="#" data-activates="nav-mobile" class="button-collapse"><i class="zmdi zmdi-menu"></i></a>
    </div>
  </nav>

<div id="main" style="position:relative;top:30px">
    <div class="wrapper-valign">
    <div class="container row center valign" style="width:350px;">
    <form action="/user/login">
        <div class="card-panel hoverable #fafafa grey lighten-5 ">
        
        <div class="input-field col s12">
            <i class="zmdi zmdi-account prefix"></i>
          <input id="user" type="text" name="user" class="validate" >
          <label for="user" data-error="wrong" >Usuário</label>
        </div>
     
        <div class="input-field col s12">
            <i class="zmdi zmdi-key prefix"></i>
          <input id="password" name="password" type="password" class="validate">
          <label for="password">Senha</label>
      </div>

        <button class="btn waves-effect waves-light #ff9e80 deep-orange accent-1" type="submit" name="action">Entrar</button></br></br>
        <a class="waves-effect waves-light btn modal-trigger" href="#modal1">Cadastrar</a>
        </div>
      </form>
    </div>
    </div>    
</br></br>
</div>

  <!-- Modal Structure -->
  <div id="modal1" class="modal">
    <div class="modal-content">
      <h4 class="caption center-align">Criar Conta</h4>
        <div class="row center">
        <form action="/user/register">
        <div class="input-field col s12">
          <input id="register-name" name="name" type="text" >
          <label for="register-name" data-error="wrong" >Nome</label>
        </div>
        <div class="input-field col s12">
          <input id="register-email" name="email" type="email" >
          <label for="register-email" data-error="wrong" >Email</label>
        </div>
        <div class="input-field col s12">
          <input id="register-password" type="password" name="password" class="validate">
          <label for="register-password">Senha</label>
        </div>
          <button class="btn waves-effect waves-light green" type="submit" name="action">Cadastrar</button></br></br>
      </div>
    </form>
    </div>
  </div>
</div>

  <footer class="page-footer #4db6ac teal lighten-2">
    <div class="container">
      <div class="row">
        <div class="col l6 s12">
          <h5 class="white-text">Company Bio</h5>
          <p class="grey-text text-lighten-4">We are a team of college students working on this project like it's our full time job. Any amount would help support and continue development on this project and is greatly appreciated.</p>


        </div>
        <div class="col l3 s12">
          <h5 class="white-text">Settings</h5>
          <ul>
            <li><a class="white-text" href="#!">Link 1</a></li>
            <li><a class="white-text" href="#!">Link 2</a></li>
            <li><a class="white-text" href="#!">Link 3</a></li>
            <li><a class="white-text" href="#!">Link 4</a></li>
          </ul>
        </div>
        <div class="col l3 s12">
          <h5 class="white-text">Connect</h5>
          <ul>
            <li><a class="white-text" href="#!">Link 1</a></li>
            <li><a class="white-text" href="#!">Link 2</a></li>
            <li><a class="white-text" href="#!">Link 3</a></li>
            <li><a class="white-text" href="#!">Link 4</a></li>
          </ul>
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
