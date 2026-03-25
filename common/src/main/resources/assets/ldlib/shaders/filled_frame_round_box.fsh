#version 150

in vec2 screenPos;
in vec2 guiPos;
in vec2 uv;

out vec4 fragColor;

uniform vec4 SquareVertex;
uniform vec4 RoundRadius;
uniform vec2 ScreenSize;
uniform vec4 FillColor;
uniform vec4 BorderColor;
uniform float Thickness;
uniform float Blur = 2.0;
uniform float GuiScale;

float sdRoundedBox(in vec2 p, in vec2 b, in vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    vec2 localUv = uv * ScreenSize / 2.0;
    localUv.x += ScreenSize.x / 2.0 - SquareVertex.x * GuiScale - ((SquareVertex.z - SquareVertex.x) * GuiScale) / 2.0 - 2.0;
    localUv.y -= ScreenSize.y / 2.0 - SquareVertex.y * GuiScale - ((SquareVertex.w - SquareVertex.y) * GuiScale) / 2.0 - 2.0;

    vec2 extents = vec2((SquareVertex.z - SquareVertex.x) / 2.0, (SquareVertex.w - SquareVertex.y) / 2.0) * GuiScale;
    vec4 radius = RoundRadius * GuiScale;

    float d = sdRoundedBox(localUv, extents, radius);

    float outerAlpha = smoothstep(Blur, 0.0, d);

    float innerEdge = -Thickness * GuiScale;
    float innerBlend = smoothstep(innerEdge + Blur, innerEdge, d);

    vec4 finalColor = mix(FillColor, BorderColor, innerBlend);

    fragColor = finalColor * outerAlpha;
}