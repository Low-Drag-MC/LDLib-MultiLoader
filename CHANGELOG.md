## v1.0.48
* Added inflate width/height values to pair with x/y offset. Allows stacking of textures of different sizes without multiple widgets.
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