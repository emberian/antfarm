/**
 * Convolution
 * by Daniel Shiffman.
 *
 * Applies a convolution matrix to a portion of an image. Move mouse to
 * apply filter to different parts of the image. Click mouse to cycle
 * through different effects (kernels).
 */

PImage img;
int effect = 0;
int w = 120;

// It's possible to convolve the image with many different
// matrices to produce different effects.  Here are some
// example kernels to try.
float[][] identity = { { 0, 0, 0 },
                       { 0, 1, 0 },
                       { 0, 0, 0 } };

float[][] darken =   { { 0,   0, 0 },
                       { 0, 0.5, 0 },
                       { 0,   0, 0 } };

float[][] lighten =  { { 0, 0, 0 },
                       { 0, 2, 0 },
                       { 0, 0, 0 } };

float[][] sharpen =  { {  0, -1,  0 },
                       { -1,  5, -1 },
                       {  0, -1,  0 } };

float[][] sharpen2 = { { -1, -1, -1 },
                       { -1,  9, -1 },
                       { -1, -1, -1 } };

float[][] box_blur = { { 1.0/9.0, 1.0/9.0, 1.0/9.0 },
                       { 1.0/9.0, 1.0/9.0, 1.0/9.0 },
                       { 1.0/9.0, 1.0/9.0, 1.0/9.0 } };

float[][] edge_det = { { 0,  1, 0 },
                       { 1, -4, 1 },
                       { 0,  1, 0 } };

float[][] emboss =   { { -2, -1, 0 },
                       { -1,  1, 1 },
                       {  0,  1, 2 } };

// collect the kernels and names into arrays for our program
float[][][] kernels = {
  identity,
  darken,
  lighten,
  sharpen,
  sharpen2,
  box_blur,
  edge_det,
  emboss
};

String[] effect_names = {
  "Identity (no change)",
  "Darken",
  "Lighten",
  "Sharpen",
  "Sharpen More",
  "Box Blur",
  "Edge Detect",
  "Emboss"
};

//
void setup() {
  size(640, 360);
  img = loadImage("moon-wide.jpg");

  noLoop();
}

// Clicking the mouse advances to the next effect
void mousePressed() {
  effect++;
  if (effect >= effect_names.length) effect = 0;

  redraw();
}

// Moving the mouse triggers a screen redraw
void mouseMoved() {
  redraw();
}
void mouseDragged() {
  redraw();
}

void draw() {
  // We're only going to process a portion of the image
  // so let's set the whole image as the background first
  image(img, 0, 0);

  // Calculate the small rectangle we will process
  int xstart = constrain(mouseX - w/2, 0, img.width);
  int ystart = constrain(mouseY - w/2, 0, img.height);
  int xend = constrain(mouseX + w/2, 0, img.width);
  int yend = constrain(mouseY + w/2, 0, img.height);
  int matrixsize = 3;
  loadPixels();
  // Begin our loop for every pixel in the smaller image
  for (int x = xstart; x < xend; x++) {
    for (int y = ystart; y < yend; y++ ) {
      color c = convolution(x, y, kernels[effect], matrixsize, img);
      int loc = x + y*img.width;
      pixels[loc] = c;
    }
  }
  updatePixels();

  textSize(24);
  text(effect_names[effect], 4, 24);
}

color convolution(int x, int y, float[][] matrix, int matrixsize, PImage img)
{
  float rtotal = 0.0;
  float gtotal = 0.0;
  float btotal = 0.0;
  int offset = matrixsize / 2;
  for (int i = 0; i < matrixsize; i++){
    for (int j= 0; j < matrixsize; j++){
      // What pixel are we testing
      int xloc = x+i-offset;
      int yloc = y+j-offset;
      int loc = xloc + img.width*yloc;
      // Make sure we haven't walked off our image, we could do better here
      loc = constrain(loc,0,img.pixels.length-1);
      // Calculate the convolution
      rtotal += (red(img.pixels[loc]) * matrix[i][j]);
      gtotal += (green(img.pixels[loc]) * matrix[i][j]);
      btotal += (blue(img.pixels[loc]) * matrix[i][j]);
    }
  }
  // Make sure RGB is within range
  rtotal = constrain(rtotal, 0, 255);
  gtotal = constrain(gtotal, 0, 255);
  btotal = constrain(btotal, 0, 255);
  // Return the resulting color
  return color(rtotal, gtotal, btotal);
}
