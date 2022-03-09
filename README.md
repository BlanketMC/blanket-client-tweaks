# Blanket client-tweaks

**Blanket** is aiming to fix many client-side bugs, also doing QoL improvements and tweaks  

## Everything is configurable 
Go into mod settings, and configure everything!  
For features, see [Config.java](https://github.com/BlanketMC/blanket-client-tweaks/blob/1.18/src/main/java/io/github/blanketmc/blanket/Config.java)  

### Usage:
Just put the mod.jar into `.minecraft/mods`, install Fabric and you're good to go!  
If you want to experiment a bit, feel free to open the mod config and change a few options.

The default options won't change vanilla behaviour, except for fixing bugs.  

## Dev env
If you want the mod in dev enviornment, just go ahead!  
You'll find it: [maven.kosmx.dev/io/github/blanketmc/blanket-client-tweaks](https://maven.kosmx.dev/io/github/blanketmc/blanket-client-tweaks/)

```groovy
repositories {
    (...)
    maven{
        name = 'KosmX\'s repo'
        url = 'https://maven.kosmx.dev/'
    }
}

dependencies {
    (...)
    
    modImplementation "io.github.blanketmc:blanket-client-tweaks:${latest_blanket_version}"
    (...)
}
```


## Modpacks
Feel free to include it in any modpack  

## Server?
The mod won't do anything on server-side, if you install it server-side, Fabric loader just won't load it.  
For server-side fixes see [Carpet-fixes](https://github.com/fxmorin/carpet-fixes)  

## Setup

For setup instructions please see the [fabric wiki page](https://fabricmc.net/wiki/tutorial:setup) that relates to the IDE that you are using.
