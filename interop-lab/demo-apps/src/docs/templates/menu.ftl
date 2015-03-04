<!-- Fixed navbar -->
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a href="http://www.mdpnp.org"><img src="images/logo.png" style="height:4em"></a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>about.html">About</a></li>
                <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>index.html">Documentation</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Resources<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li class="dropdown-header">OpenICE Downloads</li>
                        <li><a href="http://mdpnp.sourceforge.net/">Demo Apps Software</a></li>
                        <li><a href="https://github.com/jeffplourde/openice">OpenICE on github</a></li>
                        <li class="divider"></li>
                        <li class="dropdown-header">Helpful information</li>
                        <li><a href="http://www.mdpnp.org/uploads/F2761_completed_committee_draft.pdf">ICE Standard F2761</a></li>
                        <li><a href="http://portals.omg.org/dds/">OMG DDS Portal</a></li>
                        <li><a href="http://sourceforge.net/p/mdpnp/code/ci/master/tree/data-types/x73-idl/src/main/idl/ice/ice.idl">OpenICE IDL</a></li>
                    </ul>
                </li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</div>
<div class="container">