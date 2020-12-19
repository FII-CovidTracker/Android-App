import re

FILE_NAME = "logs.txt"
LOG_TABLE_HEADER = b"""
<tr>
    <th>Query</th>
    <th>Response</th>
    <th>Response Latency</th>
</tr>
"""
METRICS_TABLE_HEADER = b"""
<tr>
    <th>Response Min Latency</th>
    <th>Response Max Latency</th>
    <th>Response Avg Latency</th>
    <th>Response Req Failed</th>
</tr>
"""


def log(file_name, rows):
    with open(file_name, 'a', encoding='utf-8') as log_file:
        for row in rows:
            line = ""
            for col_index in range(len(row)):
                el = str(row[col_index])
                el = (el[:75] + '...') if len(el) > 75 else el
                line += el
                if col_index != len(row) - 1:
                    line += '¬'
            log_file.write(line)
            log_file.write('\n')


def read(file_name):
    rows = []
    with open(file_name, 'r') as log_file:
        lines = log_file.readlines()
        for line in reversed(lines):
            row = line.split('¬')
            rows += [row]
    return rows


def draw_table(header, rows):
    float_re = re.compile(r'^-?\d+(?:\.\d+)?$')
    int_re = re.compile(r'^-?\d+$')
    if rows:
        html = b"""<table>
        """
        html += header
        for row in rows:
            html += b"""<tr>
            """
            for col in row:
                el = str(col)
                if int_re.match(el) is not None:  # check if el is a int
                    el = (el[:75] + '...') if len(el) > 75 else el
                elif float_re.match(el) is not None:  # check if el is a float
                    el = "{:.4f}".format(float(el))
                html += b"""    <th>""" + el.encode('utf-8') + b"""</th>
                """

            html += b"""</tr>
            """
        html += b"""</table>
                """
        return html
    else:
        return b"<table>\n</table>"
