import math


def compute_row_metrics(latency, min, max, avg):
    avg += latency
    if latency < min:
        min = latency
    if latency > max:
        max = latency
    return min, max, avg


def get_metrics(batch_size, log_rows):
    metrics_rows = []
    number_of_rows = math.ceil(len(log_rows) / batch_size)
    for p in range(number_of_rows):
        words_min = math.inf
        words_max = 0
        words_avg = 0
        words_failed = 0
        counter = 0
        for i in range(batch_size * p, batch_size * (p + 1)):
            if i < len(log_rows) and len(log_rows[i]) == 3 and log_rows[i][2] != "failed\n":
                row = log_rows[i]
                counter += 1
                words_latency = float(row[2])
                words_min, words_max, words_avg = \
                    compute_row_metrics(words_latency, words_min, words_max, words_avg)
            elif (i < len(log_rows) and len(log_rows[i]) < 3) or \
                    (i < len(log_rows) and log_rows[i][2] == "failed\n"):
                words_failed += 1
        words_avg = words_avg / counter
        metrics_rows += [(words_min, words_max, words_avg, words_failed)]
    return metrics_rows
