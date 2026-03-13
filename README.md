# InkMaster
A really simple app that enables you to change eink refresh modes on the Mudita Kompakt.
It does not require root, you just install it, enable it in Accessability Settings and then you are good to go.
It works simply by communicating with Mudita's own Meink service and thus it is limited by what this service exposes.

The code is not pretty, but it works, at least for me. I did it while being sick at home.

## Install
As the Mudita Kompakt does not come with a browser or app store, sideloading is needed.

**On your computer**
1. Download the latest APK from releases (probably on the right side on your screen)
2. Open Mudita Center
3. Connect your Mudita Kompakt to your computer
4. Choose Media Files and app installers
5. Choose the downloaded APK (probably from your downloads folder)
6. Transfer it
7. Select the inkmaster.apk and install.

**On your Mudita Kompakt**
1. Find and open the InkMaster app.
2. It should open the Accessibility Settings, choose and toggle InkMaster on. 
3. You will ask if you want to do it, just press allow.

## Uninstall
Uninstalling is easy
1. Open the inkmaster app, this will take you to Accessibility Settings.
2. Simply toggle the InkMaster off
3. Proceed to uninstall the app as you would normally do

## FAQ

#### Is it possible to change parameters such as contrast, ink-brightness etc?
This app is limited by what is exposed by Mudita and currently it is only these modes that are available.
There is a way of doing custom modes, but there are some limitations, I might do that in a future update.

#### Will this break my phone?
No. I mean, it is always dangerous to answer this question as anything can happen, but your Mudita Kompakt already uses this functionality when you use Mudita's own apps.

## Thanks to
The code is based on several open source projects and I couldn't do this without

- A9PatchingService originally by **damianmqr** repurposed for the Hibreak Pro by **Vasu** (vbha)
  - I took the base design and layout from here and then butchered it
- InkOS by **gezimos** which is a very nice eink launcher
  - This gave me the code for calling the meink service
- Mudita Kompakt by Mudita.
