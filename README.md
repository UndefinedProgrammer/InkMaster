# InkMaster
A really simple app that enables you to change eink refresh modes on the Mudita Kompakt.
It does not require root, you just install it, enable it in Accessability Settings and then you are good to go.
It works simply by communicating with Mudita's own Meink service and thus it is limited by what this service exposes.

The code is not pretty, but it works, at least for me. I did it while being sick at home.

## Thanks to
The code is based on several open source projects and I couldn't do this without

- A9PatchingService originally by **damianmqr** repurposed for the Hibreak Pro by **Vasu** (vbha)
  - I took the base design and layout from here and then butchered it
- InkOS by **gezimos** which is a very nice eink launcher
  - This gave me the code for calling the meink service
- Mudita Kompakt by Mudita.
