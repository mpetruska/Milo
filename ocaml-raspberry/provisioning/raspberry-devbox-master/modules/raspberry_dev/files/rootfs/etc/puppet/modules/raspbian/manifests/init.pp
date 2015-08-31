 class raspbian {

  package {['locales', 'dialog']:
    ensure  => installed
  }

  package {['pkg-config', 'libglib2.0-dev']:
    ensure  => installed
  }
  
  package {['ocaml']:
    ensure => installed
  }
  
  package {['git']:
    ensure => installed
  }
  
  package {['m4']:
    ensure => installed
  }
  
  package {['curl']:
    ensure => installed
  }
  
  package {['unzip']:
    ensure => installed
  }
  
  package {['camlp4-extra']:
    ensure => installed
  }
  
}
