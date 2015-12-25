# coding=utf-8
from numpy import *
import operator


def classify0(in_x, data_set, labels, k):
    # shape 形状，比如一个数组4*2，则array.shape：（4L,2L），array.shape[0]：4，array[1]：2，array[3]就会出错
    data_set_size = data_set.shape[0]
    diff_mat = tile(in_x, (data_set_size, 1)) - data_set    # tile 将数组A重复n次，构成一个新的数组
    sq_diff_mat = diff_mat**2   # ** 即多少次方
    sq_distances = sq_diff_mat.sum(axis=1)
    distances = sq_distances**0.5
    sorted_dis_indices = distances.argsort()    # 排序
    class_count = {}
    for i in range(4):  # range(k) 到k停止
        vote_label = labels[sorted_dis_indices[i]]
        class_count[vote_label] = class_count.get(vote_label, 0) + 1
    sorted_class_count = sorted(class_count.iteritems(), key=operator.itemgetter(1), reverse=True)
    return sorted_class_count[0][0]


def create_data_set():
    group = array([[1.0, 1.1], [1.0, 1.0],  [0, 0], [0, 0.1]])
    labels = ['A', 'A', 'B', 'B']
    return group, labels

a_group, a_labels = create_data_set()
print(classify0([0.6, 0.5], a_group, a_labels, 3))
