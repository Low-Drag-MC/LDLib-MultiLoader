# ChangeLogs

## v1.0.48
* Added inflate width/height values, to pair with x/y offset. Allows stacking of textures of different sizes without multiple widgets.
* Added option for smooth progress texture, on by default to prevent breaks, how many steps to make.
* Added overflow and pop out text types.
* Added background inflate x/y values, intended for easily making the background on pop out text larger.
* Made ResourceContainer use pop out text for better readability. Also changed render order to render what's under the mouse last in ResourceContainer to fix text rendering on top of pop out text.
* Changed ResourceContainer pop out text to have background texture. Added option to SelectableWidgetGroup to render the selected overlay under the group's widgets. Added factory setter for TextTexture's backgroundTexture.
* Added optional background texture to TextTexture.
* Added NineSliceMode to ResourceBorderTexture with FIT, STRETCH, and TILE modes.
* Added default ResourceBorderTextures for vanilla button textures.
* Added new default textures to built-in.
* Added factory setter methods in ResourceTexture.
* Added test ui for SlotWidget/PlayerInventoryWidget.
* CodeEditorWidget: option for unformatted text, basically just a text pane. (UNTESTED)
* Added SliderWidget
    * Known issue: when dragging a slider, if the cursor passes a slider widget lower on the update order, the dragging will be stolen.

* Fixed bug where drag splitting an itemStack would not render correctly.
## v1.0.47
* Fixed a Mixin issue while loading massive mods

## v1.0.46
* Fixed DummyWorld memory leak
* Fixed KJSPlugin wrapper for LDLib FluidStack

## v1.0.45
* Fixed crash L2Hostility by Refactor VirtualChunk to fix. (thanks to @Taskeren)
* Fixed LabelWidget::detectAndSendChanges logic when using Component (thanks to @snylonue)

## v1.0.44
* Fixed crash L2Hostility.
* Fix UI rounding error and Crash related to SelectorWidget (thanks to @cr3eperall)

## v1.0.43
* Fixed crash L2Hostility, which tried to access level during virtual chunk <init>.
* Fixed crash with jGUI, they don't use mixin correctly
* Avoid using stream api for LinkedHashMap

## v1.0.42
* Fixed mod loading check during mixin

## v1.0.41.b
* Fixed RPCMethod crash

## v1.0.41.a
* Fixed DraggableScrollableWidget crash with client-side code

## v1.0.41
* Improve transform APIs

## v1.0.40.b
* Fixed world manager may be `null` while rendering the world scene

## v1.0.40.a
* Fixed fluid stack configurator amount limitation
* Refactored the CTM model implementation to use the loader's API
* Fixed unnecessary left-click requirement in ButtonWidget
* Fixed modular recipe widgets closing the current screen entirely when E or esc is pressed
* Fixed ModularWrapperWidget ignoring tooltips widgets that are inside scrollable widget groups

## v1.0.40
* Fixed world scene renderer issues

## v1.0.39.a
* Fixed EMI Crash

## v1.0.39
* Fixed + Improved EMI compatibility (thanks to @PrototypeTrousers)
* Fixed Number Configurator doesn't support Long
* Allow separated bg for player inventory

## v1.0.38.d
* Fixed editor resource rename doesn't work
