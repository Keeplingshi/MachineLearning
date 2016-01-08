# coding=utf-8
# 决策树算法
# createBranch()
# 检测数据集中每个子项是否属于同一类
#    If so return 类标签
#    Else
#        寻找划分数据集的最好特征
#        划分数据集
#        创建分支节点
#            for 每个划分的子子集
#                 调用函数createBranch()并返回结果到分支节点中
#        return 分支节点


from math import log
import operator


def create_data_set():
    data_set = [[1, 1, 'yes'],
               [1, 1, 'yes'],
               [1, 0, 'no'],
               [0, 1, 'no'],
               [0, 1, 'no']]
    labels = ['no surfacing', 'flippers']
    return data_set, labels


# 计算数据集的熵
def calc_shannon_ent(data_set):
    num_entries = len(data_set)
    label_counts = {}
    for featVec in data_set:
        current_label = featVec[-1]
        if current_label not in label_counts.keys():
            label_counts[current_label] = 0
        label_counts[current_label] += 1
    shannon_ent = 0.0
    for key in label_counts:
        prob = float(label_counts[key])/num_entries     # 求出某一标签所占百分比
        shannon_ent -= prob * log(prob, 2)          # shannon_ent = shannon_ent + (- prob * log(prob, 2))
    return shannon_ent


# 按照给定的特征划分数据集
# 三个参数：待划分的数据集，划分数据集的特征（第多少列序号），需要返回的特征的值（即axis列的其中一个值value）
def split_data_set(data_set, axis, value):
    ret_data_set = []
    for featVec in data_set:
        if featVec[axis] == value:
            reduced_feat_vec = featVec[:axis]     # chop out axis used for splitting
            reduced_feat_vec.extend(featVec[axis+1:])
            ret_data_set.append(reduced_feat_vec)
    return ret_data_set


# 该函数实现选取特征值，
# 划分数据集，计算得出最好的划分数据集的特征（返回的是索引值）
def choose_best_feature_to_split(data_set):
    num_features = len(data_set[0]) - 1
    base_entropy = calc_shannon_ent(data_set)
    best_info_gain = 0.0
    best_feature = -1
    for i in range(num_features):
        # 获取第i列的所有元素
        feat_list = [example[i] for example in data_set]    # for example in data_set  一个循环
        unique_vals = set(feat_list)    # 去除掉重复对象
        new_entropy = 0.0
        for value in unique_vals:
            sub_data_set = split_data_set(data_set, i, value)
            prob = len(sub_data_set)/float(len(data_set))
            new_entropy += prob * calc_shannon_ent(sub_data_set)    # 根据该特征某一值所占百分比计算该特征的熵
        # 信息增益
        info_gain = base_entropy - new_entropy
        # 取信息增益大的特征作为划分依据
        if(info_gain > best_info_gain ):
            best_info_gain = info_gain
            best_feature = i
    return best_feature


# 返回出现次数最多的分类名称
def majority_cnt(class_list):
    class_count={}
    for vote in class_list:
        if vote not in class_count.keys(): class_count[vote] = 0
        class_count[vote] += 1
    sorted_class_count = sorted(class_count.iteritems(), key=operator.itemgetter(1), reverse=True)
    return sorted_class_count[0][0]


# 创建树
def create_tree(data_set, labels):
    # 获取最后一列，即分类结果
    class_list = [example[-1] for example in data_set]
    # 类别相同则停止划分
    if class_list.count(class_list[0]) == len(class_list):
        return class_list[0]
    # 用完了所有特征
    if len(data_set[0]) == 1:
        return majority_cnt(class_list)
    # 获取最好的特征索引
    best_feat = choose_best_feature_to_split(data_set)
    # 根据索引获取特征标签
    best_feat_label = labels[best_feat]
    my_tree = {best_feat_label:{}}
    del(labels[best_feat])
    # 获取该特征的所有值
    feat_values = [example[best_feat] for example in data_set]
    # 获取该特征所有不同的值
    unique_vals = set(feat_values)
    for value in unique_vals:
        sub_labels = labels[:]  # 复制类标签
        my_tree[best_feat_label][value] = create_tree(split_data_set(data_set, best_feat, value), sub_labels)
    return my_tree


def classify(input_tree, feat_labels, test_vec):
    first_str = input_tree.keys()[0]
    second_dict = input_tree[first_str]
    feat_index = feat_labels.index(first_str)
    key = test_vec[feat_index]
    value_of_feat = second_dict[key]
    if isinstance(value_of_feat, dict):
        class_label = classify(value_of_feat, feat_labels, test_vec)
    else: class_label = value_of_feat
    return class_label


def store_tree(input_tree, filename):
    import pickle
    fw = open(filename, 'w')
    pickle.dump(input_tree, fw)
    fw.close()


def grab_tree(filename):
    import pickle
    fr = open(filename)
    return pickle.load(fr)

