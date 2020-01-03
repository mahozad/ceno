[![Build Status](https://travis-ci.org/mahozad/ceno.svg?branch=master)](https://travis-ci.org/mahozad/ceno)

![Screenshot](docs/screenshot.png)

# Ceno 
Ceno social news website

### Notes

The following plugins are used in IntelliJ IDEA for developing this project:
- Lombok: To add support for code generated by Lombok
- File Watchers [(follow this guide)](https://www.jetbrains.com/help/idea/compressing-css.html)
  - SCSS: To automatically convert SCSS files to CSS
  - YUI Compressor CSS: To automatically minify CSS files
  - YUI Compressor JS: To automatically minify JavaScript files
- LiveEdit: To automatically refresh the page when CSS or HTML is modified
- WakaTime: To keep track of coding activity. [View your statistics here.](https://wakatime.com/dashboard)

#

[Google style guides](https://google.github.io/styleguide/) is followed for coding (Java, HTML, CSS, JavaScript).

#

To manage vendor-provided CSS and JavaScript files (like jQuery, Bootstrap and so on)
and be able to add/change them in project dependencies, we can use [**webjars**](https://www.webjars.org/all).

#

[Can I use](https://caniuse.com/) could be used to check if browsers support a feature. 

#

Good charting libraries for html:
- [Chart.js](https://github.com/chartjs/Chart.js)
- [Recharts](http://recharts.org/en-US)
- [Chartist](http://gionkunz.github.io/chartist-js/)

#

As of version 2018.3, IntelliJ can sort CSS properties.
To enable it go to `Settings -> Editor -> Code Style -> Style Sheets -> Arrangement tab`.
To do the rearrangement, from main menu, select `Code -> Show Reformat File Dialog` and check `Rearrange code` checkbox.
Checkbox state is remembered, so next time the code is reformatted it is also rearranged.

#

The following picture shows the effect of different placements of `<script>` element ([see this post for more info](https://stackoverflow.com/a/24070373)):
![`<script>` element placement](docs/script-element-placement.png)
