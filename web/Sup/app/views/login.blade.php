@extends("index")
@section("main")
@parent
<div class="wrapper-valign">
    <div class="container row center valign" style="width:350px;">
     <form action="/user/web-login">
      <div class="card-panel hoverable #fafafa grey lighten-5 ">
        <div class="input-field col s12">
            <i class="zmdi zmdi-account prefix"></i>
          <input id="user" type="text" name="email" class="validate" >
          <label for="user" data-error="wrong" >Email</label>
        </div>

        <div class="input-field col s12">
            <i class="zmdi zmdi-key prefix"></i>
          <input id="password" name="password" type="password" class="validate">
          <label for="password">Senha</label>
        </div>

        <button class="btn waves-effect waves-light #ff9e80 deep-orange accent-1" type="submit" name="action">Entrar</button></br></br>
        <a class="waves-effect waves-light btn modal-trigger" href="#modal1">Cadastrar</a>

        <div>
          {{Session::get('msg')}}
        </div>
      </div>
    </form>
    </div>
</div>    
</br></br>

  <!-- Modal Structure -->
  <div id="modal1" class="modal">
    <div class="modal-content">
      <h4 class="caption center-align">Criar Conta</h4>
        <div class="row center">
        <form action="/user/web-register">
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
@stop
