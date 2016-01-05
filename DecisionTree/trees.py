# coding=utf-8
from math import log
import operator


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


def create_data_set():
    data_set = [[1, 1, 'yes'],
               [1, 1, 'yes'],
               [1, 0, 'no'],
               [0, 1, 'no'],
               [0, 1, 'no']]
    labels = ['no surfacing', 'flippers']
    return data_set, labels


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


# 该函数实现选取特征值，划分数据集，计算得出最好的划分数据集的特征（返回的是索引值）
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


def majority_cnt(class_list):
    class_count={}
    for vote in class_list:
        if vote not in class_count.keys(): class_count[vote] = 0
        class_count[vote] += 1
    sorted_class_count = sorted(class_count.iteritems(), key=operator.itemgetter(1), reverse=True)
    return sorted_class_count[0][0]


# 创建树
def create_tree(data_set, labels):
    class_list = [example[-1] for example in data_set]
    # 类别相同则停止划分
    if class_list.count(class_list[0]) == len(class_list):
        return class_list[0]
    # 用完了所有特征
    if len(data_set[0]) == 1:
        return majority_cnt(class_list)
    best_feat = choose_best_feature_to_split(data_set)
    best_feat_label = labels[best_feat]
    my_tree = {best_feat_label:{}}
    del(labels[best_feat])
    feat_values = [example[best_feat] for example in data_set]
    unique_vals = set(feat_values)
    for value in unique_vals:
        sub_labels = labels[:]  # 复制类标签
        my_tree[best_feat_label][value] = create_tree(split_data_set(data_set, best_feat, value), sub_labels)
    return my_tree


def classify(inputTree,featLabels,testVec):
    firstStr = inputTree.keys()[0]
    secondDict = inputTree[firstStr]
    featIndex = featLabels.index(firstStr)
    key = testVec[featIndex]
    valueOfFeat = secondDict[key]
    if isinstance(valueOfFeat, dict):
        classLabel = classify(valueOfFeat, featLabels, testVec)
    else: classLabel = valueOfFeat
    return classLabel


def storeTree(inputTree,filename):
    import pickle
    fw = open(filename,'w')
    pickle.dump(inputTree,fw)
    fw.close()


def grabTree(filename):
    import pickle
    fr = open(filename)
    return pickle.load(fr)


a, b = create_data_set()
print a
# print calc_shannon_ent(a)
# print split_data_set(a, 0, 1)
# print choose_best_feature_to_split(a)
tree = create_tree(a, b)
print tree

