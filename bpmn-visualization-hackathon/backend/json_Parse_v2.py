
import xml.sax.saxutils as saxutils
from collections import deque
import math

DEFAULT_DIMS = {
    "startEvent": {"width": 36, "height": 36}, "endEvent": {"width": 36, "height": 36},
    "messageEventDefinition": {"width": 36, "height": 36}, "boundaryEvent": {"width": 36, "height": 36},
    "intermediateThrowEvent": {"width": 36, "height": 36}, "intermediateCatchEvent": {"width": 36, "height": 36},
    "task": {"width": 100, "height": 80}, "userTask": {"width": 100, "height": 80},
    "serviceTask": {"width": 100, "height": 80}, "scriptTask": {"width": 100, "height": 80},
    "subProcess": {"width": 100, "height": 80}, "exclusiveGateway": {"width": 50, "height": 50},
    "parallelGateway": {"width": 50, "height": 50}, "inclusiveGateway": {"width": 50, "height": 50},
    "eventBasedGateway": {"width": 50, "height": 50}, "pool": {"width": 600, "height": 100},
    "lane": {"width": 500, "height": 100}, "dataObject": {"width": 50, "height": 70},
    "textAnnotation": {"width": 100, "height": 30}, "dataStoreReference": {"width": 50, "height": 50}
}

ELEMENT_MAX_HEIGHT = 80
ELEMENT_MAX_WIDTH = 100
SPACE_BETWEEN_ELEMENTS_IN_LEVEL = 40
INITIAL_X = 50
INITIAL_Y = 100
HORIZONTAL_GAP = 80
VERTICAL_GAP = 50


def get_element_dims(element_data):
    elem_type = element_data['type']
    default_size = DEFAULT_DIMS.get(elem_type, {"width": 100, "height": 80})
    width = element_data.get('width') or default_size['width']
    height = element_data.get('height') or default_size['height']
    return {"width": width, "height": height}

def generate_process_xml(elements, connections):
    xml_parts = []
    pools = [e for e in elements if e['type'] == 'pool']
    message_flows = [mf for mf in connections if mf['type'] == 'messageFlow']

    if pools:
        collaboration_id = "Collaboration_1"
        xml_parts.append(f'<collaboration id="{collaboration_id}">')
        for pool in pools:
            participant_id = pool['id']
            lanes = [e for e in elements if e['type'] == 'lane']
            if len([e for e in lanes if e['parentId'] == pool['id']]) == 0:
                xml_parts.append(
                    f'<participant id="{participant_id}" name="{saxutils.escape(pool.get("name", ""))}"/>'
                )
                continue
            process_ref = f"Process_{participant_id}"
            xml_parts.append(
                f'<participant id="{participant_id}" name="{saxutils.escape(pool.get("name", ""))}" processRef="{process_ref}" />'
            )
        for message_flow in message_flows:
            xml_parts.append(
                f'<messageFlow id="{message_flow["id"]}" sourceRef="{message_flow["sourceRef"]}" targetRef="{message_flow["targetRef"]}" />'
            )
        xml_parts.append('</collaboration>')

        for pool in pools:
            pool_elements = get_all_children(pool['id'], elements)
            lanes = [e for e in pool_elements if e['type'] == 'lane']
            flow_elements = [e for e in pool_elements if e['type'] not in ['pool', 'lane']]
            flow_ids = [e['id'] for e in flow_elements]
            process_id = f"Process_{pool['id']}"
            xml_parts.append(f'<process id="{process_id}" isExecutable="false">')

            pool_lanes = [lane for lane in lanes if lane['parentId'] == pool['id']]
            if pool_lanes:
                xml_parts.append(f'<laneSet id="LaneSet_{pool["id"]}">')
                for lane in pool_lanes:
                    lane_id = saxutils.escape(lane['id'])
                    lane_name = saxutils.escape(lane['name'])
                    xml_parts.append(f'<lane id="{lane_id}" name="{lane_name}" >')

                    for elem in flow_elements:
                        if elem.get('parentId') == lane['id']:
                            xml_parts.append(f'<flowNodeRef>{saxutils.escape(elem["id"])}</flowNodeRef>')
                    xml_parts.append('</lane>')
                xml_parts.append('</laneSet>')

            for elem in flow_elements:
                if elem.get('parentId') in [lane['id'] for lane in pool_lanes] or elem.get('parentId') == pool['id']:
                    elem_type = elem['type']
                    elem_id = saxutils.escape(elem['id'])
                    elem_name = saxutils.escape(elem.get('name', ''))

                    event_type = elem.get('eventType')
                    additional_attrs = []

                    if 'cancelActivity' in elem:
                        cancel_activity = str(elem['cancelActivity']).lower()
                        additional_attrs.append(f'cancelActivity="{cancel_activity}"')

                    if 'attachedToRef' in elem:
                        attached_ref = saxutils.escape(elem['attachedToRef'])
                        additional_attrs.append(f'attachedToRef="{attached_ref}"')

                    attrs_str = ' '.join(additional_attrs)

                    if event_type:
                        definition_id = f"{event_type}_{elem_id}"
                        xml_parts.append(
                            f'<{elem_type} id="{elem_id}" name="{elem_name}"  {attrs_str}>\n'
                            f'  <bpmn:{event_type} id="{definition_id}" />\n'
                            f'</{elem_type}>'
                        )
                    else:
                        xml_parts.append(f'<{elem_type} id="{elem_id}" name="{elem_name}"  {attrs_str}/>')

            for conn in connections:
                if (conn['type'] == 'sequenceFlow' or conn['type'] == 'messageFlow') and conn['sourceRef'] in flow_ids:
                    conn_id = saxutils.escape(conn['id'])
                    source_ref = saxutils.escape(conn['sourceRef'])
                    target_ref = saxutils.escape(conn['targetRef'])
                    conn_name = saxutils.escape(conn.get('name') or '')
                    attributes = f' name="{conn_name}"' if conn_name else ""

                    xml_parts.append(
                        f'<{conn["type"]} id="{conn_id}" sourceRef="{source_ref}" targetRef="{target_ref}" {attributes}/>'
                    )

            xml_parts.append('</process>')
    return '\n'.join(xml_parts)

def get_all_children(parent_id, elements):
    children = []
    for el in elements:
        if el.get('parentId') == parent_id:
            children.append(el)
            children.extend(get_all_children(el['id'], elements))
    return children

def update_layout(layout, elements):
    pools = [e for e in elements if e['type'] == 'pool']

    for pool in pools:
        pool_id = pool['id']
        pool_children = get_all_children(pool_id, elements)

        pool_x = layout[pool_id]['x']
        pool_y = layout[pool_id]['y']
        max_x = pool_x
        max_y = pool_y

        for child in pool_children:
            child_id = child['id']
            if child_id in layout:
                child_layout = layout[child_id]
                current_x = child_layout['x'] + child_layout['width']
                if current_x > max_x:
                    max_x = current_x
                current_y = child_layout['y'] + child_layout['height']
                if current_y > max_y:
                    max_y = current_y

        if max_x != pool_x:
            layout[pool_id]['width'] = max_x - pool_x + 50

        if max_y != pool_y:
            layout[pool_id]['height'] = max_y - pool_y

        for el in elements:
            if el.get('parentId') == pool_id:
                el_id = el['id']
                if el_id in layout:
                    layout[el_id]['width'] = max_x - layout[el_id]['x'] + 50

    return layout

def updated_bounds(element_id, elements, layout):
    element_layout = layout.get(element_id)
    if not element_layout:
        return
    el_x = element_layout['x']
    el_y = element_layout['y']
    el_width = element_layout['width']
    el_height = element_layout['height']
    el = [elem for elem in elements if elem.get('id') == element_id]
    children = [elem for elem in elements if elem.get('parentId') == element_id]
    max_right = el_x + el_width
    max_bottom = el_y + el_height
    for child in children:
        child_id = child['id']
        child_info = layout.get(child_id)
        if not child_info:
            continue

        child_dims = get_element_dims(child)
        child_width = child_dims['width']
        child_height = child_dims['height']

        child_right = child_info['x'] + child_width
        child_bottom = child_info['y'] + child_height

        if child_right > max_right:
            max_right = child_right
        if child_bottom > max_bottom:
            max_bottom = child_bottom

    new_width = max_right - el_x
    new_height = max_bottom - el_y

    if new_width > el_width or new_height > el_height:
        element_layout['width'] = new_width
        element_layout['height'] = new_height
        if 'parentId' in el[0]:
            children = [elem for elem in elements if elem.get('parentId') == el[0]['parentId']]
            updated_bounds(el[0]['parentId'], elements, layout)


def build_tree_from_elements_and_connections(elements, connections):
    element_map = {el['id']: el for el in elements}

    parent_to_children_ids = {el_id: [] for el_id in element_map}
    all_target_ids = set()

    for conn in connections:
        source_id = conn.get('sourceRef')
        target_id = conn.get('targetRef')

        if source_id in element_map and target_id in element_map:
            parent_to_children_ids[source_id].append(target_id)
            all_target_ids.add(target_id)

    root_ids = [el_id for el_id in element_map if el_id not in all_target_ids]

    def build_node(element_id, visited_in_branch):
        if element_id in visited_in_branch:
            return element_map[element_id].copy()

        node = element_map[element_id].copy()
        visited_in_branch.add(element_id)

        children_ids = parent_to_children_ids.get(element_id, [])
        if children_ids:
            children_nodes = []
            for child_id in children_ids:
                child_node = build_node(child_id, visited_in_branch.copy())
                if child_node:
                    children_nodes.append(child_node)

            if children_nodes:
                node['children'] = children_nodes

        return node

    tree = []
    for root_id in root_ids:
        tree.append(build_node(root_id, set()))

    return tree



def get_all_levels(tree):
    levels_dict = {}
    if not tree:
        return levels_dict
    queue = deque([(node, 0) for node in tree])
    for node, level in queue:
        if node and node.get('id'):
            if level not in levels_dict:
                levels_dict[level] = []
            is_duplicate = any(n.get('id') == node.get('id') for n in levels_dict.get(level, []))
            if not is_duplicate:
                levels_dict[level].append(node)
    final_levels_dict = {}
    queue_bfs = deque([(node, 0) for node in tree])
    visited_ids_in_bfs = set(
        node.get('id') for node, _ in queue_bfs if node and node.get('id'))
    while queue_bfs:
        current_node, current_level = queue_bfs.popleft()
        if not current_node: continue
        node_id = current_node.get('id')
        if not node_id: continue
        if current_level not in final_levels_dict:
            final_levels_dict[current_level] = []
        final_levels_dict[current_level].append(current_node)
        if 'children' in current_node:
            for child in current_node['children']:
                child_id = child.get('id')
                if child_id and child_id not in visited_ids_in_bfs:
                    visited_ids_in_bfs.add(child_id)
                    queue_bfs.append((child, current_level + 1))
    return final_levels_dict

def remove_duplicates_from_levels(all_levels_data):
    deduplicated_levels = {}
    for level, node_list in all_levels_data.items():
        seen_ids = set()
        unique_nodes = []
        for node in node_list:
            node_id = node.get('id')
            if node_id is not None and node_id not in seen_ids:
                unique_nodes.append(node)
                seen_ids.add(node_id)
        deduplicated_levels[level] = unique_nodes
    return deduplicated_levels

def find_level_with_max_elements(levels_data):
    if not levels_data:
        return 0, None
    max_count = -1
    level_with_max = None
    for level, id_list in levels_data.items():
        current_count = len(id_list)
        if current_count > max_count:
            max_count = current_count
            level_with_max = level
    if max_count == -1:
        first_level = next(iter(levels_data))
        return 0, first_level
    return max_count, level_with_max

def filter_levels_data_for_lane(global_levels_data, target_lane_id):
    lane_specific_levels = {}
    for level, node_list in global_levels_data.items():
        filtered_nodes_for_level = []
        for node in node_list:
            if node.get('parentId') == target_lane_id:
                filtered_nodes_for_level.append(node)
        if filtered_nodes_for_level:
            lane_specific_levels[level] = filtered_nodes_for_level
    return lane_specific_levels

def calculate_hierarchical_layout(elements, connections):
    layout = {}
    pools = [e for e in elements if e['type'] == 'pool']
    lanes = [e for e in elements if e['type'] == 'lane']
    flow_elements = [e for e in elements if e['type'] not in ['pool', 'lane']]
    resulting_tree = build_tree_from_elements_and_connections(flow_elements, connections)
    all_levels_data = get_all_levels(resulting_tree)
    unique_levels_data = remove_duplicates_from_levels(all_levels_data)
    pool_x = INITIAL_X
    pool_y = INITIAL_Y
    for pool in pools:
        dims = get_element_dims(pool)
        layout[pool['id']] = {
            'x': pool_x,
            'y': pool_y,
            'width': dims['width'],
            'height': dims['height']
        }
        lane_y = pool_y
        pool_lanes = [lane for lane in lanes if lane['parentId'] == pool['id']]
        for lane in pool_lanes:
            x = layout[pool['id']]['x'] + 30
            y = lane_y
            layout[lane['id']] = {
                'x': x,
                'y': y,
                'width': layout[pool['id']]['width'] - 20,
                'height': 0
            }

            lane_data = filter_levels_data_for_lane(unique_levels_data, lane['id'])

            max_stack_count, _ = find_level_with_max_elements(lane_data)
            lane_height = SPACE_BETWEEN_ELEMENTS_IN_LEVEL
            i = 0
            while i < max_stack_count:
                lane_height += ELEMENT_MAX_HEIGHT + SPACE_BETWEEN_ELEMENTS_IN_LEVEL
                i += 1
            layout[lane['id']]['height'] = lane_height
            layout[pool['id']]['height'] += lane_height
            lane_y += lane_height

            for i in sorted(lane_data.keys()):
                space_between_elements = (lane_height - ELEMENT_MAX_HEIGHT * len(lane_data[i])) / (len(lane_data[i]) + 1)
                current_element_y = layout[lane['id']]['y'] + space_between_elements
                j = 0
                while j < len(lane_data[i]):
                    elem_dims = get_element_dims(lane_data[i][j])
                    elem_x = i * 150 + ((ELEMENT_MAX_WIDTH - elem_dims['width']) / 2) + HORIZONTAL_GAP + 50
                    elem_y = current_element_y + (ELEMENT_MAX_HEIGHT - elem_dims['height']) / 2
                    layout[lane_data[i][j]['id']] = {
                        'x': elem_x,
                        'y': elem_y,
                        'width': elem_dims['width'],
                        'height': elem_dims['height'],
                        "level": i
                    }
                    current_element_y += ELEMENT_MAX_HEIGHT + space_between_elements
                    j += 1
            updated_bounds(lane['id'], elements, layout)
        if lane_y != pool_y:
            pool_y = lane_y + VERTICAL_GAP
        else:
            pool_y += dims['height'] + VERTICAL_GAP
    update_layout(layout, elements)
    return layout


def generate_seq_flows(layout, connections, xml_parts, elements):
    MIN_SEGMENT = 10
    PADDING = 10
    def simplify_points(points):
        if len(points) < 3:
            return points

        simplified = [points[0]]
        for i in range(1, len(points) - 1):
            p_prev = simplified[-1]
            p_curr = points[i]
            p_next = points[i + 1]

            is_horizontal_collinear = (p_prev[1] == p_curr[1] == p_next[1])
            is_vertical_collinear = (p_prev[0] == p_curr[0] == p_next[0])

            if not (is_horizontal_collinear or is_vertical_collinear):
                simplified.append(p_curr)

        simplified.append(points[-1])

        if len(simplified) >= 2 and simplified[0] == simplified[1]:
            simplified.pop(1)
        if len(simplified) >= 2 and simplified[-2] == simplified[-1]:
            simplified.pop()

        if len(points) >= 2 and len(simplified) < 2:
            return points[:2]

        return simplified

    def calculate_orthogonal_points(src_point, tgt_point, src_side, tgt_side):
        raw_points = [src_point]

        if src_side in ['left', 'right']:
            dir_x = 1 if src_side == 'right' else -1
            p1 = (src_point[0] + dir_x * MIN_SEGMENT, src_point[1])
        else:
            dir_y = 1 if src_side == 'bottom' else -1
            p1 = (src_point[0], src_point[1] + dir_y * MIN_SEGMENT)

        if p1 != src_point:
            raw_points.append(p1)

        if tgt_side in ['left', 'right']:
            dir_x = -1 if tgt_side == 'left' else 1
            tgt_ext = (tgt_point[0] + dir_x * MIN_SEGMENT, tgt_point[1])
        else:
            dir_y = -1 if tgt_side == 'top' else 1
            tgt_ext = (tgt_point[0], tgt_point[1] + dir_y * MIN_SEGMENT)

        last_point = raw_points[-1]
        is_aligned = (last_point[0] == tgt_ext[0]) or (last_point[1] == tgt_ext[1])

        if not is_aligned:
            if src_side in ['left', 'right']:
                corner_point = (tgt_ext[0], last_point[1])
            else:
                corner_point = (last_point[0], tgt_ext[1])

            if corner_point != last_point:
                raw_points.append(corner_point)

        if tgt_ext != raw_points[-1]:
            raw_points.append(tgt_ext)

        if tgt_point != raw_points[-1]:
            raw_points.append(tgt_point)

        return raw_points

    def get_all_possible_connections(src, tgt):
        sx, sy, sw, sh = src['x'], src['y'], src['width'], src['height']
        tx, ty, tw, th = tgt['x'], tgt['y'], tgt['width'], tgt['height']

        src_center_x = sx + sw / 2
        src_center_y = sy + sh / 2
        tgt_center_x = tx + tw / 2
        tgt_center_y = ty + th / 2


        src_sides = [
            ('right', (sx + sw, src_center_y)),
            ('left', (sx, src_center_y)),
            ('top', (src_center_x, sy)),
            ('bottom', (src_center_x, sy + sh))
        ]


        tgt_sides = [
            ('left', (tx, tgt_center_y)),
            ('right', (tx + tw, tgt_center_y)),
            ('top', (tgt_center_x, ty)),
            ('bottom', (tgt_center_x, ty + th))
        ]

        all_combinations = []
        for src_side, src_point in src_sides:
            for tgt_side, tgt_point in tgt_sides:
                all_combinations.append((src_point, tgt_point, src_side, tgt_side))

        return all_combinations

    def segment_intersects_element(a, b, element, is_source_or_target):
        padding = 0 if is_source_or_target else PADDING
        elem_rect = (
            element['x'] - padding,
            element['y'] - padding,
            element['width'] + 2 * padding,
            element['height'] + 2 * padding
        )

        seg_left = min(a[0], b[0])
        seg_right = max(a[0], b[0])
        seg_top = min(a[1], b[1])
        seg_bottom = max(a[1], b[1])

        no_overlap = (
                elem_rect[0] > seg_right or
                elem_rect[0] + elem_rect[2] < seg_left or
                elem_rect[1] > seg_bottom or
                elem_rect[1] + elem_rect[3] < seg_top
        )

        return not no_overlap

    def generate_detour(a, b, element, is_source_or_target):
        padding = 0 if is_source_or_target else PADDING
        ex = element['x'] - padding
        ey = element['y'] - padding
        ew = element['width'] + 2 * padding
        eh = element['height'] + 2 * padding

        is_horizontal = a[1] == b[1]
        is_vertical = a[0] == b[0]

        left = ex
        right = ex + ew
        top = ey
        bottom = ey + eh

        if is_horizontal:
            y = a[1]
            if top <= y <= bottom:
                if y < (top + bottom) / 2:
                    detour_y = top - PADDING
                else:
                    detour_y = bottom + PADDING

                return [
                    a,
                    (a[0], detour_y),
                    (b[0], detour_y),
                    b
                ]

        elif is_vertical:
            x = a[0]
            if left <= x <= right:
                if x < (left + right) / 2:
                    detour_x = left - PADDING
                else:
                    detour_x = right + PADDING

                return [
                    a,
                    (detour_x, a[1]),
                    (detour_x, b[1]),
                    b
                ]

        return [a, b]

    def process_route(route_points, elements_to_check, source_id, target_id, processed_elements=None, depth=0):
        MAX_DEPTH = 5
        if depth > MAX_DEPTH:
            return route_points

        processed_elements = processed_elements or set()
        new_route = []
        i = 0
        while i < len(route_points) - 1:
            a, b = route_points[i], route_points[i + 1]
            collision = False
            collision_element = None

            for elem_id, elem in elements_to_check:
                if elem_id in (source_id, target_id):
                    continue
                if elem_id in processed_elements:
                    continue
                if segment_intersects_element(a, b, elem, False):
                    collision = True
                    collision_element = (elem_id, elem)
                    break

            if collision:
                elem_id, elem = collision_element
                processed_elements.add(elem_id)
                detour = generate_detour(a, b, elem, False)

                processed_detour = process_route(
                    detour,
                    elements_to_check,
                    source_id,
                    target_id,
                    processed_elements.copy(),
                    depth + 1
                )

                new_route.extend(processed_detour[:-1])
                new_last = processed_detour[-1]

                for j in range(i + 1, len(route_points)):
                    if route_points[j] == new_last:
                        i = j
                        break
                else:
                    i = len(route_points) - 1
            else:
                new_route.append(a)
                i += 1

        new_route.append(route_points[-1])
        return new_route

    def calculate_length(points):
        length = 0.0
        for i in range(len(points) - 1):
            x1, y1 = points[i]
            x2, y2 = points[i + 1]
            length += math.hypot(x2 - x1, y2 - y1)
        return length

    for conn in connections:
        conn_id = saxutils.escape(conn['id'])
        source_id = conn['sourceRef']
        target_id = conn['targetRef']
        edge_id = f"BPMNEdge_{conn_id}"

        source_layout = layout.get(source_id)
        target_layout = layout.get(target_id)

        xml_parts.append(f'    <bpmndi:BPMNEdge id="{edge_id}" bpmnElement="{conn_id}">')

        if source_layout and target_layout:
            def get_parent_pool_id(elem_id):
                elem = next((e for e in elements if e['id'] == elem_id), None)
                while elem and 'parentId' in elem:
                    parent = next((e for e in elements if e['id'] == elem['parentId']), None)
                    if parent and parent['type'] == 'pool':
                        return parent['id']
                    elem = parent
                return None

            parent_pools = set()
            if parent_pool_source := get_parent_pool_id(source_id):
                parent_pools.add(parent_pool_source)
            if parent_pool_target := get_parent_pool_id(target_id):
                parent_pools.add(parent_pool_target)

            all_lane_ids = {e['id'] for e in elements if e['type'] == 'lane'}

            elements_to_check = [
                (elem_id, elem) for elem_id, elem in layout.items()
                if elem_id not in parent_pools
                   and elem_id not in all_lane_ids
            ]

            all_connection_variants = get_all_possible_connections(source_layout, target_layout)

            best_route = None
            best_score = (float('inf'), float('inf'))

            for variant in all_connection_variants:
                src_point, tgt_point, src_side, tgt_side = variant
                route_points = calculate_orthogonal_points(src_point, tgt_point, src_side, tgt_side)
                processed_route = process_route(route_points, elements_to_check, source_id, target_id)
                simplified_route = (processed_route)

                if len(simplified_route) < 2:
                    continue

                num_points = len(simplified_route)
                length = calculate_length(simplified_route)
                current_score = (num_points, length)

                if current_score <= best_score:
                    best_score = current_score
                    best_route = simplified_route



                route_points = best_route


            route_points = simplify_points(route_points)

            for point in route_points:
                xml_parts.append(f'      <di:waypoint x="{point[0]:.1f}" y="{point[1]:.1f}" />')
        else:
            xml_parts.append('      <di:waypoint x="0" y="0" />')
            xml_parts.append('      <di:waypoint x="0" y="0" />')

        xml_parts.append('    </bpmndi:BPMNEdge>')


def generate_diagram_xml(elements, layout, connections):

    xml_parts = ['<bpmndi:BPMNDiagram id="BPMNDiagram_1">',
                 '<bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Collaboration_1">']

    for pool in (e for e in elements if e['type'] == 'pool'):
        bounds = layout[pool['id']]
        xml_parts.append(
            f'<bpmndi:BPMNShape id="BPMNShape_{pool["id"]}" bpmnElement="{pool["id"]}">'
            f'<dc:Bounds x="{bounds["x"]}" y="{bounds["y"]}" width="{bounds["width"]}" height="{bounds["height"]}" />'
            '</bpmndi:BPMNShape>'
        )

    for lane in (e for e in elements if e['type'] == 'lane'):
        bounds = layout[lane['id']]
        xml_parts.append(
            f'<bpmndi:BPMNShape id="BPMNShape_{lane["id"]}" bpmnElement="{lane["id"]}">'
            f'<dc:Bounds x="{bounds["x"]}" y="{bounds["y"]}" width="{bounds["width"]}" height="{bounds["height"]}" />'
            '</bpmndi:BPMNShape>'
        )

    for elem in (e for e in elements if e['type'] not in ['pool', 'lane']):
        if elem['id'] not in layout:
            continue
        bounds = layout[elem['id']]
        add_options = "isMarkerVisible=\"true\"" if elem["type"] == "exclusiveGateway" else ""
        xml_parts.append(
            f'<bpmndi:BPMNShape id="BPMNShape_{elem["id"]}" bpmnElement="{elem["id"]}" {add_options}>'
            f'<dc:Bounds x="{bounds["x"]}" y="{bounds["y"]}" width="{bounds["width"]}" height="{bounds["height"]}" />'
            '</bpmndi:BPMNShape>'
        )

    generate_seq_flows(layout, connections, xml_parts, elements)
    xml_parts.extend(['</bpmndi:BPMNPlane>', '</bpmndi:BPMNDiagram>'])
    return '\n'.join(xml_parts)


def get_bpmn(sample_data):
    layout = calculate_hierarchical_layout(sample_data['elements'], sample_data['connections'])
    process_xml = generate_process_xml(sample_data['elements'], sample_data['connections'])
    diagram_xml = generate_diagram_xml(sample_data['elements'], layout, sample_data['connections'])
    full_xml = f'''<?xml version="1.0" encoding="UTF-8"?>
    <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                xmlns:di="http://www.omg.org/spec/DD/20100524/DI">
    {process_xml}
    {diagram_xml}
    </definitions>'''
    return full_xml

