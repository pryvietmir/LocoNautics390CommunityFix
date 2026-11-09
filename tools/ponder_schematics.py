"""Generates Ponder schematics (vanilla structure NBT, gzipped) for loconautics_fixes."""
import gzip, struct, os, sys

OUT = sys.argv[1]

def _s(b, v):
    e = v.encode('utf-8'); b += struct.pack('>H', len(e)) + e
def _tag(b, t, v):
    if t == 3: b += struct.pack('>i', v)
    elif t == 8: _s(b, v)
    elif t == 9:
        et, items = v
        b += struct.pack('>bi', et if items else 0, len(items))
        for it in items: _tag(b, et, it)
    elif t == 10:
        for k, (tt, vv) in v.items():
            b += struct.pack('>b', tt); _s(b, k); _tag(b, tt, vv)
        b += b'\x00'
def C(**kw): return (10, kw)
def I(v): return (3, v)
def S(v): return (8, v)
def L(t, items): return (9, (t, items))

def write(name, size, blocks):
    """blocks: dict (x,y,z) -> (block_id, {props})"""
    palette, pidx, out = [], {}, []
    for pos in sorted(blocks):
        bid, props = blocks[pos]
        key = (bid, tuple(sorted(props.items())))
        if key not in pidx:
            pidx[key] = len(palette)
            entry = {'Name': S(bid)}
            if props: entry['Properties'] = C(**{k: S(str(v)) for k, v in props.items()})
            palette.append(entry)
        out.append({'pos': L(3, list(pos)), 'state': I(pidx[key])})
    root = {
        'DataVersion': I(3955),
        'size': L(3, list(size)),
        'palette': (9, (10, palette)),
        'blocks': (9, (10, out)),
        'entities': (9, (10, [])),
    }
    b = bytearray(); b += struct.pack('>b', 10); _s(b, ''); _tag(b, 10, root)
    path = os.path.join(OUT, name + '.nbt')
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with gzip.open(path, 'wb') as f: f.write(bytes(b))
    print('wrote', path, len(out), 'blocks')

def base(n=5):
    return {(x, 0, z): ('minecraft:snow_block' if (x + z) % 2 == 0 else 'minecraft:white_concrete', {})
            for x in range(n) for z in range(n)}

L_ = 'loconautics:'
# --- transmission: shaft in (x0..1) -> transmission (2) -> shaft out (x3..4); speed signal on north, direction on south
b = base()
for x in (0, 1, 3, 4): b[(x, 1, 2)] = ('create:shaft', {'axis': 'x'})
b[(2, 1, 2)] = (L_ + 'transmission', {'axis': 'x', 'powered': 'false', 'plus_side': 'false'})
b[(2, 1, 1)] = ('minecraft:redstone_block', {})
b[(2, 1, 3)] = ('minecraft:lever', {'face': 'floor', 'facing': 'north', 'powered': 'false'})
write('transmission', (5, 2, 5), b)

# --- analog controller on a casing
b = base()
b[(2, 1, 2)] = ('create:andesite_casing', {})
b[(2, 2, 2)] = (L_ + 'red_analog_controller', {'facing': 'south'})
b[(0, 1, 2)] = (L_ + 'transmission', {'axis': 'x', 'powered': 'false', 'plus_side': 'false'})
b[(4, 1, 2)] = (L_ + 'bearing_axle', {'facing': 'west'})
write('analog_controller', (5, 3, 5), b)

# --- bearing axle: track along z, bogey on it, axle beside the bogey facing it, shaft into its back
b = base()
for z in range(5): b[(2, 1, z)] = ('create:track', {'shape': 'zo', 'turn': 'false', 'waterlogged': 'false'})
b[(2, 2, 2)] = ('create:small_bogey', {'axis': 'z', 'waterlogged': 'false'})
b[(3, 2, 2)] = (L_ + 'bearing_axle', {'facing': 'east'})
b[(4, 2, 2)] = ('create:shaft', {'axis': 'x'})
write('bearing_axle', (5, 3, 5), b)

# --- handbrake on a casing, lever beside it
b = base()
b[(2, 1, 2)] = ('create:andesite_casing', {})
b[(2, 2, 2)] = (L_ + 'handbrake', {'facing': 'south', 'powered': 'false'})
b[(1, 2, 2)] = ('minecraft:lever', {'face': 'floor', 'facing': 'east', 'powered': 'false'})
b[(1, 1, 2)] = ('create:andesite_casing', {})
write('handbrake', (5, 3, 5), b)

# --- knuckles: two car ends facing each other with a one block gap
b = base()
b[(0, 1, 2)] = ('create:andesite_casing', {}); b[(1, 1, 2)] = (L_ + 'knuckle', {'facing': 'east', 'connected': 'false'})
b[(3, 1, 2)] = (L_ + 'knuckle', {'facing': 'west', 'connected': 'false'}); b[(4, 1, 2)] = ('create:andesite_casing', {})
write('knuckle', (5, 2, 5), b)

# --- screw link headstocks facing each other
b = base()
b[(0, 1, 2)] = ('create:andesite_casing', {}); b[(1, 1, 2)] = (L_ + 'screw_link', {'facing': 'east'})
b[(3, 1, 2)] = (L_ + 'screw_link', {'facing': 'west'}); b[(4, 1, 2)] = ('create:andesite_casing', {})
write('screw_link', (5, 2, 5), b)

# --- copycat headstock on a casing
b = base()
b[(1, 1, 2)] = ('create:andesite_casing', {}); b[(2, 1, 2)] = (L_ + 'copycat_headstock', {'facing': 'east'})
write('copycat_headstock', (5, 2, 5), b)

# --- turntable: shaft from below, bearing facing up, 3x3 plate, track on top
b = base()
b[(2, 1, 2)] = ('create:shaft', {'axis': 'y'})
b[(2, 2, 2)] = (L_ + 'turntable_bearing', {'facing': 'up', 'assembled': 'false', 'powered': 'false'})
for x in (1, 2, 3):
    for z in (1, 2, 3): b[(x, 3, z)] = (L_ + 'turntable_plate', {'facing': 'up'})
for z in (1, 2, 3): b[(2, 4, z)] = ('create:track', {'shape': 'zo', 'turn': 'false', 'waterlogged': 'false'})
b[(0, 1, 2)] = ('create:andesite_casing', {})
b[(0, 2, 2)] = ('minecraft:lever', {'face': 'floor', 'facing': 'east', 'powered': 'false'})
b[(1, 1, 2)] = ('create:andesite_casing', {})
b[(1, 2, 2)] = ('minecraft:redstone_wire', {'east': 'side', 'west': 'side', 'north': 'none', 'south': 'none', 'power': '0'})
write('turntable', (5, 5, 5), b)

# --- industrial fluids: pipe -> pump (cog driven) -> pipe -> tank
b = base()
b[(0, 1, 2)] = (L_ + 'industrial_pipe', {'east': 'true', 'west': 'true', 'north': 'false', 'south': 'false', 'up': 'false', 'down': 'false'})
b[(1, 1, 2)] = (L_ + 'industrial_pump', {'facing': 'east'})
b[(2, 1, 2)] = (L_ + 'industrial_pipe', {'east': 'true', 'west': 'true', 'north': 'false', 'south': 'false', 'up': 'false', 'down': 'false'})
b[(3, 1, 2)] = (L_ + 'industrial_fluid_tank', {})
b[(1, 1, 3)] = ('create:cogwheel', {'axis': 'x'})
b[(0, 1, 3)] = ('create:shaft', {'axis': 'x'})
write('industrial', (5, 2, 5), b)

# --- hose connector on a tank, nozzle socket on a tank
b = base()
b[(1, 1, 2)] = ('create:fluid_tank', {'bottom': 'true', 'top': 'true', 'shape': 'window'})
b[(2, 1, 2)] = (L_ + 'hose_connector', {'facing': 'west'})
b[(3, 1, 2)] = ('create:fluid_tank', {'bottom': 'true', 'top': 'true', 'shape': 'window'})
b[(3, 2, 2)] = (L_ + 'fluid_hatch_gray', {'facing': 'down', 'open': 'false', 'lid_hinge': '0', 'raised': 'false'})
write('hoses', (5, 3, 5), b)
