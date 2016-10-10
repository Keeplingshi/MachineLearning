import tensorflow as tf

import MNIST.input_data as input_data

# 为获取训练集与测试集数据
mnist = input_data.read_data_sets("../MNIST/", one_hot=True)

# 定义变量
x = tf.placeholder("float", [None, 784])
# 权重
W = tf.Variable(tf.zeros([784, 10]))
# 偏置量
b = tf.Variable(tf.zeros([10]))

# tf.matmul(matrix1, matrix2) 代表矩阵乘法
# 实现模型
y = tf.nn.softmax(tf.matmul(x, W) + b)

# 评估训练结果
# 用于存储正确结果
y_ = tf.placeholder("float", [None, 10])
# 交叉熵
cross_entropy = -tf.reduce_sum(y_*tf.log(y))
# 梯度下降，最小化交叉熵
train_step = tf.train.GradientDescentOptimizer(0.01).minimize(cross_entropy)

# 初始化所有变量
init = tf.initialize_all_variables()
# 启动模型
sess = tf.Session()
sess.run(init)

# 训练模型，循环训练1000次
for i in range(1000):
    batch_xs, batch_ys = mnist.train.next_batch(100)
    sess.run(train_step, feed_dict={x: batch_xs, y_: batch_ys})

# 计算正确率
correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, "float"))
print(sess.run(accuracy, feed_dict={x: mnist.test.images, y_: mnist.test.labels}))
