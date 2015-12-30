# coding=utf-8
from numpy import *
import operator
import matplotlib.pyplot as plt


def classify0(in_x, data_set, labels, k):
    # shape 形状，比如一个数组4*2，则array.shape：（4L,2L），array.shape[0]：4，array[1]：2，array[3]就会出错
    data_set_size = data_set.shape[0]
    diff_mat = tile(in_x, (data_set_size, 1)) - data_set  # tile 将数组A重复n次，构成一个新的数组
    sq_diff_mat = diff_mat ** 2  # ** 即多少次方
    sq_distances = sq_diff_mat.sum(axis=1)
    distances = sq_distances ** 0.5
    sorted_dis_indices = distances.argsort()  # 排序
    class_count = {}
    for i in range(4):  # range(k) 到k停止
        vote_label = labels[sorted_dis_indices[i]]
        class_count[vote_label] = class_count.get(vote_label, 0) + 1
    sorted_class_count = sorted(class_count.iteritems(), key=operator.itemgetter(1), reverse=True)
    return sorted_class_count[0][0]


def create_data_set():
    group = array([[1.0, 1.1], [1.0, 1.0], [0, 0], [0, 0.1]])
    labels = ['A', 'A', 'B', 'B']
    return group, labels


def file_to_matrix(file_name):
    fr = open(file_name)
    array_lines = fr.readlines()
    number_of_lines = len(array_lines)
    return_mat = zeros((number_of_lines, 3))
    class_label_vector = []
    index = 0
    for line in array_lines:
        line = line.strip()
        list_from_line = line.split('\t')
        return_mat[index, :] = list_from_line[0: 3]
        class_label_vector.append(int(list_from_line[-1]))
        index += 1
    return return_mat, class_label_vector


# 归一化特征值
def auto_norm(data_set):
    min_val = data_set.min(0)
    max_val = data_set.max(0)
    ranges = max_val - min_val
    m = data_set.shape[0]
    norm_data_set = data_set - tile(min_val, (m, 1))
    norm_data_set = norm_data_set / tile(ranges, (m, 1))
    return norm_data_set, ranges, min_val


def dating_class_test():
    ho_ratio = 0.50  # hold out 10%
    dating_data_mat, dating_labels = file_to_matrix('datingTestSet2.txt')  # load data_set from file
    norm_mat, ranges, min_vals = auto_norm(dating_data_mat)
    m = norm_mat.shape[0]
    num_test_vecs = int(m * ho_ratio)
    error_count = 0.0
    for i in range(num_test_vecs):
        classifier_result = classify0(norm_mat[i, :], norm_mat[num_test_vecs:m, :], dating_labels[num_test_vecs:m], 3)
        print "the classifier came back with: %d, the real answer is: %d" % (classifier_result, dating_labels[i])
        if (classifier_result != dating_labels[i]): error_count += 1.0
    print "the total error rate is: %f" % (error_count / float(num_test_vecs))
    print error_count


print dating_class_test()